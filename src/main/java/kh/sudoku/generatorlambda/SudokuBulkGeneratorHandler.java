package kh.sudoku.generatorlambda;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class SudokuBulkGeneratorHandler implements RequestHandler<Map<String,String>, String>{

    @Override
    public String handleRequest(Map<String,String> event, Context context)
    {
        String result = null;
        
        //
        
        result = "success";
        return result;
    }
}
