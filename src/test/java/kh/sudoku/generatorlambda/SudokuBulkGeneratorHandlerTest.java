package kh.sudoku.generatorlambda;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class SudokuBulkGeneratorHandlerTest {

    @Test
    public void testGetFormattedISODate() {
        String result = new SudokuBulkGeneratorHandler().getFormattedISODate();
        System.out.println(result);
        assertNotNull(result);
    }
    
}
