package jscope.samples;

import java.io.IOException;
import java.io.Serializable;

/**
 * <p>Javadoc.</p>
 * @author Alexandre Dutra
 */
public class Sample extends Object implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private final String[] field1 = new String[]{"**/*.java", "**/*.xml", "**/*.properties"};

    //@SCOPE OP-3456 BEGIN
    /**
     * <p>Javadoc</p>
     * @see "http://foo.com/bar"
     */
    public void execute() {

        //@SCOPE OP-4567 BEGIN
        this.execute2(null);
        //@SCOPE END

        /*
         * multiline comment
         */

    }
    //@SCOPE END

    //@SCOPE OP-4567 BEGIN
    /**
     * <p>Javadoc</p>
     */
    public void execute2(@SuppressWarnings("unused") final Object param) {

    }
    //@SCOPE END

    //simple comment

    /**
     * <p>Javadoc</p>
     * @throws IOException
     */
    @SuppressWarnings("unused")
    public void execute3(final Object param, final Object param2) throws IOException {

    }
}
