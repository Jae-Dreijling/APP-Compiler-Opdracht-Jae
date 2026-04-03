package nl.han.ica.icss.Evaluator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import nl.han.ica.icss.Pipeline;

public class EvaluatorTest {

    @Test
    public void testMultiplyScalarPixel() {
        String input = """
            h1 {
                width: 2 * 10px;
            }
        """;

        Pipeline pipeline = new Pipeline();

        pipeline.parseString(input);
        pipeline.check();
        pipeline.transform();

        String output = pipeline.generate();

        assertTrue(output.contains("20px"));
    }

    @Test
    public void testSubtract() {
        String input = """
            h1 {
                width: 20px - 5px;
            }
        """;

        Pipeline pipeline = new Pipeline();

        pipeline.parseString(input);
        pipeline.check();
        pipeline.transform();

        String output = pipeline.generate();

        assertTrue(output.contains("15px"));
    }
}