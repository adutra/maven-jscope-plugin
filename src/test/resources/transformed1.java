package jscope.test;

import org.apache.maven.plugin.AbstractMojo;

/**
 * <p>Javadoc.</p>
 *
 * @goal generateBusinessDoc
 */
public class Test extends AbstractTest implements Foo {

    private final String[] field1 = new String[]{"**/*.java", "**/*.xml", "**/*.properties"};

    //@SCOPE BASELINE BEGIN
//@UNSCOPED@//
//@UNSCOPED@//    /**
//@UNSCOPED@//     * <p>Javadoc</p>
//@UNSCOPED@//     * @see "http://foo.com/bar"
//@UNSCOPED@//     */
//@UNSCOPED@//    public void execute() {
//@UNSCOPED@//    }
//@UNSCOPED@//
    //@SCOPE END
    
    //@SCOPE OP-3456 BEGIN
//@UNSCOPED@//    /**
//@UNSCOPED@//     * <p>Javadoc</p>
//@UNSCOPED@//     * @see "http://foo.com/bar"
//@UNSCOPED@//     */
//@UNSCOPED@//    public void execute() {
//@UNSCOPED@//
        //@SCOPE OP-4567 BEGIN
        //@UNSCOPED@//            execute2(null);
        //@SCOPE END
//@UNSCOPED@//        
//@UNSCOPED@//    }
    //@SCOPE END

    //@SCOPE OP-4567 BEGIN
    /**
     * <p>Javadoc</p>
     * @throws MojoFailureException
     */
    public void execute2(Object param) {

    }
    //@SCOPE END
    
    //simple comment
    
    /**
     * <p>Javadoc</p>
     * @throws MojoFailureException
     */
    public void execute3(Object param, Object param2) throws MojoExecutionException, MojoFailureException {

    }
}
