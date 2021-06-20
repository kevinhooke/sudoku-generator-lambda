package kh.sudoku.getpuzzlelambda;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;

import kh.sudoku.db.SudokuPuzzles;

public class GetPuzzleHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger LOG = LogManager.getLogger(GetPuzzleHandler.class);
    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		LOG.info("received: {}", input);
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		
		String uuid = UUID.randomUUID().toString();
		
		QueryRequest query = new QueryRequest();
		
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(uuid));
        //TODO parameterize this
        eav.put(":val2", new AttributeValue().withS("HARD"));
		
		DynamoDBQueryExpression<SudokuPuzzles> queryExpression_greaterThan = new DynamoDBQueryExpression<SudokuPuzzles>()
	            .withKeyConditionExpression("id > :val1 and difficulty = :val2")
	            .withExpressionAttributeValues(eav)
	            .withLimit(1);

	      DynamoDBQueryExpression<SudokuPuzzles> queryExpression_lessThan = new DynamoDBQueryExpression<SudokuPuzzles>()
	                .withKeyConditionExpression("id < :val1 and difficulty = :val2")
	                .withExpressionAttributeValues(eav)
	                .withLimit(1);

		
	    List<SudokuPuzzles> puzzles = mapper.query(SudokuPuzzles.class, queryExpression_greaterThan);
	    if(puzzles.size() == 1) {
	        System.out.println("Found first record on >");
	    }
	    else {
	        System.out.println("No match on > query, trying <");
	        puzzles = mapper.query(SudokuPuzzles.class, queryExpression_lessThan);
	    }
	    
		Map<String, Object> result = new HashMap<>();
		result.put("puzzles", puzzles);
		Response responseBody = new Response("Puzzle result", result);
		return ApiGatewayResponse.builder()
				.setStatusCode(200)
				.setObjectBody(responseBody)
				.build();
	}
}
