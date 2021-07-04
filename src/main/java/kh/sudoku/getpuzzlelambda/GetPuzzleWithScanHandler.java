package kh.sudoku.getpuzzlelambda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;

import kh.sudoku.db.SudokuPuzzles;

public class GetPuzzleWithScanHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(GetPuzzleWithScanHandler.class);
    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOG.info("received: {}", input);

        DynamoDBMapper mapper = new DynamoDBMapper(client);

        String uuid = UUID.randomUUID().toString();

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(uuid));
        // TODO parameterize this
        eav.put(":val2", new AttributeValue().withS("HARD"));

        DynamoDBScanExpression queryExpression_greaterThan = new DynamoDBScanExpression()
                .withFilterExpression("id > :val1 and difficulty = :val2")
                .withExpressionAttributeValues(eav)
                // limit is the number of rows scanned, not items retrieved
                .withLimit(1);
        
        DynamoDBScanExpression queryExpression_lessThan = new DynamoDBScanExpression()
                .withFilterExpression("id < :val1 and difficulty = :val2")
                .withExpressionAttributeValues(eav)
                .withLimit(1);
        
        List<SudokuPuzzles> puzzles = mapper.scan(SudokuPuzzles.class, queryExpression_greaterThan);

        Map<String, Object> result = new HashMap<>();
        
        System.out.println("Puzzles received in scan, > scan count: " + puzzles.size());
        result.put("gt-scan-count", puzzles.size());
        
        if (puzzles.size() > 0) {
            System.out.println("Found first record on >");
            result.put("resultsOnGtSearchSearch", "true");
            result.put("resultsOnLtSearchSearch", "not-used");
        } else {
            System.out.println("No match on > query, trying < ...");
            result.put("resultsOnGtSearchSearch", "false");

            puzzles = mapper.scan(SudokuPuzzles.class, queryExpression_lessThan);
            System.out.println("Puzzles received in page, < queryPage count: " + puzzles.size());
            result.put("lt-scan-count", puzzles.size());

            if(puzzles.size() > 0) {
                result.put("resultsOnLtSearchSearch", "true");
            }
            else {
                result.put("resultsOnLtSearchSearch", "false");
            }
        }

        System.out.println("... retrieved rows: " + puzzles.size());


        if (puzzles.size() > 0) {
            result.put("puzzlesInList", puzzles.size());
            result.put("puzzle", puzzles.get(0));
        } else {
            result.put("puzzle", new ArrayList<String>());
        }

        Response responseBody = new Response("Puzzle result", result);
        return ApiGatewayResponse.builder().setStatusCode(200).setObjectBody(responseBody).build();
    }
}
