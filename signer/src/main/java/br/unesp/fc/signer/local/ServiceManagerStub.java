package br.unesp.fc.signer.local;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.jnlp.UnavailableServiceException;

/**
 *
 * @author demitrius
 */
public class ServiceManagerStub implements javax.jnlp.ServiceManagerStub {

    private final static Map<String, Object> lookupTable = new HashMap<>();

    public static void add(String key, Object object) {
        lookupTable.put(key, object);
    }

    @Override
    public Object lookup(String string) throws UnavailableServiceException {
        return lookupTable.get(string);
    }

    @Override
    public String[] getServiceNames() {
        return lookupTable.keySet().stream().collect(Collectors.toList()).toArray(new String[0]);
    }

}
