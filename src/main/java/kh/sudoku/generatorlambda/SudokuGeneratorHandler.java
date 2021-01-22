package kh.sudoku.generatorlambda;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;

import kh.sudoku.PuzzleResults;
import kh.sudoku.generator.SudokuGenerator;


public class SudokuGeneratorHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger LOG = LogManager.getLogger(SudokuGeneratorHandler.class);

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		LOG.info("received: {}", input);
		
		SudokuGenerator generator = new SudokuGenerator();

        PuzzleResults results = generator.generate(50);

        List<List<String>> generatedPuzzles = results.getResults();
        for (List<String> shorthand : generatedPuzzles) {
            System.out.println(shorthand);
        }

        System.out.println("Puzzle valid: " + results.isValidPuzzle());
		
		Response responseBody = new Response("SudokuGeneratorLambda handleReques()", input);
		return ApiGatewayResponse.builder()
				.setStatusCode(200)
				.setObjectBody(responseBody)
				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
				.build();
	}
}
