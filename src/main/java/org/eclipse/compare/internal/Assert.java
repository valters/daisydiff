package org.eclipse.compare.internal;

public class Assert {

    public static void isTrue( final boolean cond ) {
        if( ! cond ) {
            throw new IllegalStateException( "Assertion failed." );
        }
    }

}
