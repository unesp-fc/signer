/*
 * @(#)JnlpDownloadServlet.java	1.10 07/03/15
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

package br.unesp.fc.signer.webstart;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Servlet class is an implementation of JNLP Specification's
 * Download Protocols.
 * <p>
 * All requests to this servlet is in the form of HTTP GET commands.
 * The parameters that are needed are:
 * <ul>
 * <li><code>arch</code>,</li>
 * <li><code>os</code>,</li>
 * <li><code>locale</code>,</li>
 * <li><code>version-id</code> or <code>platform-version-id</code>,</li>
 * <li><code>current-version-id</code>,</li>
 * <li><code>known-platforms</code></li>
 * </ul>
 *
 * @version 1.8 01/23/03
 */
public class JnlpDownloadServlet
        extends HttpServlet
{

    private static final Logger _log = LoggerFactory.getLogger(JnlpDownloadServlet.class);

    // Localization
    private static ResourceBundle _resourceBundle = null;

    // Servlet configuration
    private static final String PARAM_JNLP_EXTENSION = "jnlp-extension";

    private static final String PARAM_JAR_EXTENSION = "jar-extension";

    private static final String PARAM_JNLP_FILE_HANDLER_HOOK = "jnlp-file-handler-hook";

    // Servlet configuration
    private JnlpFileHandler _jnlpFileHandler = null;

    private JarDiffHandler _jarDiffHandler = null;

    private ResourceCatalog _resourceCatalog = null;

    /**
     * Initialize servlet
     */
    public void init( ServletConfig config )
            throws ServletException
    {
        super.init( config );

        // Setup logging
        _log.debug( "Initializing" );

        // Get extension from Servlet configuration, or use default
        JnlpResource.setDefaultExtensions( config.getInitParameter( PARAM_JNLP_EXTENSION ),
                                           config.getInitParameter( PARAM_JAR_EXTENSION ) );

        JnlpFileHandlerHook hook = createHook( config.getInitParameter(PARAM_JNLP_FILE_HANDLER_HOOK) );
        _jnlpFileHandler = new JnlpFileHandler( config.getServletContext(), hook );
        _jarDiffHandler = new JarDiffHandler( config.getServletContext() );
        _resourceCatalog = new ResourceCatalog( config.getServletContext() );
    }

    /**
     * Creates the instance of the configured {@link JnlpFileHandlerHook}
     * 
     * @param hookClass class
     * @return the post-processor instance, never {@code null}
     */
    private JnlpFileHandlerHook createHook( String hookClass ) {
        if ( hookClass != null )
            try {
                return Class.forName( hookClass ).asSubclass( JnlpFileHandlerHook.class ).newInstance();
            } catch ( InstantiationException | IllegalAccessException | ClassNotFoundException e ) {
                _log.warn( "Failed to load implementing class for JnlpPostProcessor: {}", hookClass, e );
            }
        return JnlpFileHandlerHook.IDENTITY;
    }

    public static synchronized ResourceBundle getResourceBundle()
    {
        if ( _resourceBundle == null )
        {
            _resourceBundle = ResourceBundle.getBundle( "jnlp/sample/servlet/resources/strings" );
        }
        return _resourceBundle;
    }


    public void doHead( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException
    {
        handleRequest( request, response, true );
    }

    /**
     * We handle get requests too - eventhough the spec. only requeres POST requests
     */
    public void doGet( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException
    {
        handleRequest( request, response, false );
    }

    private void handleRequest( HttpServletRequest request, HttpServletResponse response, boolean isHead )
            throws IOException
    {
        String requestStr = request.getRequestURI();
        if ( request.getQueryString() != null )
        {
            requestStr += "?" + request.getQueryString().trim();
        }

        // Parse HTTP request
        DownloadRequest dreq = new DownloadRequest( request.getServletContext(), request );
        if ( _log.isInfoEnabled() )
        {
            _log.info( "Request: {}", requestStr );
            _log.info( "User-Agent: {}", request.getHeader( "User-Agent" ) );
        }
        if ( _log.isDebugEnabled() )
        {
            _log.debug( dreq.toString() );
        }

        long ifModifiedSince = request.getDateHeader( "If-Modified-Since" );

        // Check if it is a valid request
        try
        {
            // Check if the request is valid
            validateRequest( dreq );

            // Decide what resource to return
            JnlpResource jnlpres = locateResource( dreq );
            _log.debug( "JnlpResource: " + jnlpres );

            if ( _log.isInfoEnabled() )
            {
                _log.info( "Resource returned: {}", jnlpres.getPath() );
            }

            DownloadResponse dres;

            if ( isHead )
            {

                int cl = jnlpres.getResource().openConnection().getContentLength();

                // head request response
                dres = DownloadResponse.getHeadRequestResponse( jnlpres.getMimeType(), jnlpres.getVersionId(),
                                                                jnlpres.getLastModified(), cl );

            }
            else if ( ifModifiedSince != -1 && ( ifModifiedSince / 1000 ) >= ( jnlpres.getLastModified() / 1000 ) )
            {
                // We divide the value returned by getLastModified here by 1000
                // because if protocol is HTTP, last 3 digits will always be 
                // zero.  However, if protocol is JNDI, that's not the case.
                // so we divide the value by 1000 to remove the last 3 digits
                // before comparison

                // return 304 not modified if possible
                _log.debug( "return 304 Not modified" );
                dres = DownloadResponse.getNotModifiedResponse();

            }
            else
            {

                // Return selected resource
                dres = constructResponse( jnlpres, dreq );
            }

            dres.sendRespond( response );

        }
        catch ( ErrorResponseException ere )
        {
            if ( _log.isInfoEnabled() )
            {
                _log.info( "Error code returned for request: {}", requestStr );
            }
            if ( _log.isDebugEnabled() )
            {
                _log.debug( "Response: " + ere.toString() );
            }
            // Return response from exception
            ere.getDownloadResponse().sendRespond( response );
        }
        catch ( Throwable e )
        {
            _log.error( "Internal error:", e );
            response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
        }
    }

    /**
     * Make sure that it is a valid request. This is also the place to implement the
     * reverse IP lookup
     */
    private void validateRequest( DownloadRequest dreq )
            throws ErrorResponseException
    {
        String path = dreq.getPath();
        if ( path.endsWith( ResourceCatalog.VERSION_XML_FILENAME ) || path.indexOf( "__" ) != -1 )
        {
            throw new ErrorResponseException( DownloadResponse.getNoContentResponse() );
        }
    }

    /**
     * Interprets the download request and convert it into a resource that is
     * part of the Web Archive.
     */
    private JnlpResource locateResource( DownloadRequest dreq )
            throws IOException, ErrorResponseException
    {
        if ( dreq.getVersion() == null )
        {
            return handleBasicDownload( dreq );
        }
        else
        {
            return handleVersionRequest( dreq );
        }
    }

    private JnlpResource handleBasicDownload( DownloadRequest dreq )
            throws ErrorResponseException, IOException
    {
        _log.debug( "Basic Protocol lookup" );
        // Do not return directory names for basic protocol
        if ( dreq.getPath() == null || dreq.getPath().endsWith( "/" ) )
        {
            throw new ErrorResponseException( DownloadResponse.getNoContentResponse() );
        }
        // Lookup resource
        JnlpResource jnlpres = new JnlpResource( dreq.getServletContext(), dreq.getPath() );
        if ( !jnlpres.exists() )
        {
            throw new ErrorResponseException( DownloadResponse.getNoContentResponse() );
        }
        return jnlpres;
    }

    private JnlpResource handleVersionRequest( DownloadRequest dreq )
            throws IOException, ErrorResponseException
    {
        _log.debug( "Version-based/Extension based lookup" );
        return _resourceCatalog.lookupResource( dreq );
    }

    /**
     * Given a DownloadPath and a DownloadRequest, it constructs the data stream to return
     * to the requester
     */
    private DownloadResponse constructResponse( JnlpResource jnlpres, DownloadRequest dreq )
            throws IOException
    {
        String path = jnlpres.getPath();
        if ( jnlpres.isJnlpFile() )
        {
            // It is a JNLP file. It need to be macro-expanded, so it is handled differently
            boolean supportQuery = JarDiffHandler.isJavawsVersion( dreq, "1.5+" );
            _log.debug( "SupportQuery in Href: " + supportQuery );

            // only support query string in href for 1.5 and above
            if ( supportQuery )
            {
                return _jnlpFileHandler.getJnlpFileEx( jnlpres, dreq );
            }
            else
            {
                return _jnlpFileHandler.getJnlpFile( jnlpres, dreq );
            }
        }

        // Check if a JARDiff can be returned
        if ( dreq.getCurrentVersionId() != null && jnlpres.isJarFile() )
        {
            DownloadResponse response = _jarDiffHandler.getJarDiffEntry( _resourceCatalog, dreq, jnlpres );
            if ( response != null )
            {
                _log.info( "JarDiff returned for request" );
                return response;
            }
        }

        // check and see if we can use pack resource
        JnlpResource jr =
                new JnlpResource( dreq.getServletContext(), jnlpres.getName(), jnlpres.getVersionId(), jnlpres.getOSList(),
                                  jnlpres.getArchList(), jnlpres.getLocaleList(), jnlpres.getPath(),
                                  jnlpres.getReturnVersionId(), dreq.getEncoding() );

        _log.debug( "Real resource returned: " + jr );

        // Return WAR file resource
        return DownloadResponse.getFileDownloadResponse( jr.getResource(), jr.getMimeType(), jr.getLastModified(),
                                                         jr.getReturnVersionId() );
    }
}


