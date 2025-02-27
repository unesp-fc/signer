package br.unesp.fc.signer.debug;

import javax.swing.JComponent;
import javax.swing.RepaintManager;

public class VerboseRepaintManager extends RepaintManager {

    @Override
    public synchronized void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
        System.out.println("adding DirtyRegion: "+c.getName()+", "+x+","+y+" "+w+"x"+h);
        super.addDirtyRegion(c,x,y,w,h);
    }

    @Override
    public void paintDirtyRegions() {
       // Unfortunately most of the RepaintManager state is package
       // private and not accessible from the subclass at the moment,
       // so we can't print more info about what's being painted.
        System.out.println("painting DirtyRegions");
        super.paintDirtyRegions();
    }

}
