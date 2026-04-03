package nl.han.ica.icss.Checker;

import org.junit.jupiter.api.Test;

import nl.han.ica.icss.Pipeline;

import static org.junit.jupiter.api.Assertions.*;

public class CheckerTest {

    @Test
    public void testUndefinedVariable() {
        String input = """
            h1 {
                width: $unknown;
            }
        """;

        Pipeline pipeline = new Pipeline();

        pipeline.parseString(input);
        pipeline.check();

        assertFalse(pipeline.getErrors().isEmpty());
    }

    @Test
    public void testInvalidOperation() {
        String input = """
            h1 {
                width: 10px + 5%;
            }
        """;

        Pipeline pipeline = new Pipeline();

        pipeline.parseString(input);
        pipeline.check();

        assertFalse(pipeline.getErrors().isEmpty());
    }

    @Test
    public void testInvalidIfCondition() {
        String input = """
            h1 {
                if [10px] {
                    width: 10px;
                }
            }
        """;

        Pipeline pipeline = new Pipeline();

        pipeline.parseString(input);
        pipeline.check();

        assertFalse(pipeline.getErrors().isEmpty());
    }
}