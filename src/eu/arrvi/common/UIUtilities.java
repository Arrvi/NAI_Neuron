package eu.arrvi.common;

import javax.swing.*;

/**
 * Collection of some shortcuts for common GUI tasks
 */
public class UIUtilities {
    /**
     * Sets global look and feel. First it tries to set system look and feel. If it fails, second try is to set
     * cross-platform look and feel. Default is left at second failure.
     */
    public static void setSystemLookAndFeel () {
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        } catch ( ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e ) {
            System.err.println( "Cannot set system look and feel. Trying cross-platform." );
            try {
                UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
            } catch ( ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e1 ) {
                System.err.println( "Cannot set cross-platform look and feel neither. Leaving default." );
            }
        }
    }
    
    /**
     * Common procedure for showing frame after GUI construction. Packs, sets location to center (null),
     * sets default close operation to exit and shows given frame.
     *
     * @param frame frame to be shown
     */
    public static void packAndShow ( JFrame frame ) {
        packAndShow( frame, true );
    }
    
    /**
     * Common procedure for showing frame after GUI construction. Packs, sets location to center (null)
     * and shows given frame. Optionally sets default close operation to exit.
     *
     * @param frame       frame to be shown
     * @param exitOnClose if true, default close operation is set to exit
     */
    public static void packAndShow ( JFrame frame, boolean exitOnClose ) {
        frame.pack();
        frame.setLocationRelativeTo( null );
        if ( exitOnClose ) frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
