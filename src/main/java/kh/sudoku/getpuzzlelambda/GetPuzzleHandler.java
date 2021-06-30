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
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
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

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(uuid));
        // TODO parameterize this
        eav.put(":val2", new AttributeValue().withS("HARD"));

        // TODO if the key is difficulty and sort is the id, this would make it
        // easier to query for any
        // puzzle of a given difficulty, and not scan until we find one

        DynamoDBQueryExpression<SudokuPuzzles> queryExpression_greaterThan = new DynamoDBQueryExpression<SudokuPuzzles>()
                .withKeyConditionExpression("difficulty = :val2 and id > :val1")
                .withExpressionAttributeValues(eav)
                .withIndexName("PuzzleByDifficultyIndex")
                .withConsistentRead(false);
                //.withLimit(1);

        DynamoDBQueryExpression<SudokuPuzzles> queryExpression_lessThan = new DynamoDBQueryExpression<SudokuPuzzles>()
                .withKeyConditionExpression("difficulty = :val2 and id < :val1")
                .withExpressionAttributeValues(eav)
                .withIndexName("PuzzleByDifficultyIndex")
                .withConsistentRead(false);
                //.withLimit(1);
        
        // DynamoDBScanExpression queryExpression_greaterThan = new
        // DynamoDBScanExpression()
        // .withFilterExpression("id > :val1 and difficulty = :val2")
        // .withExpressionAttributeValues(eav)
        // //limit is the number of rows scanned, not items retrieved
        // .withLimit(1);
        //
        // DynamoDBScanExpression queryExpression_lessThan = new
        // DynamoDBScanExpression()
        // .withFilterExpression("id < :val1 and difficulty = :val2")
        // .withExpressionAttributeValues(eav)
        // .withLimit(1);
        // List<SudokuPuzzles> puzzles = mapper.scan(SudokuPuzzles.class,
        // queryExpression_greaterThan);

        // List<SudokuPuzzles> puzzles = mapper.query(SudokuPuzzles.class,
        // queryExpression_greaterThan);

        // use .queryPage() to retrieve a subset of matching values
        QueryResultPage<SudokuPuzzles> puzzlesResultsPage = mapper.queryPage(SudokuPuzzles.class,
                queryExpression_greaterThan);

        Map<String, Object> result = new HashMap<>();
        
        int puzzlesInPage = puzzlesResultsPage.getCount();

        System.out.println("Puzzles received in page, > queryPage count: " + puzzlesInPage);
        result.put("gt-page-count", puzzlesInPage);
        
        List<SudokuPuzzles> puzzles = puzzlesResultsPage.getResults();

        if (puzzles.size() > 0) {
            System.out.println("Found first record on >");
            result.put("resultsOnGtSearchSearch", "true");
            result.put("resultsOnLtSearchSearch", "not-used");
        } else {
            System.out.println("No match on > query, trying < ...");
            result.put("resultsOnGtSearchSearch", "false");

            // puzzles = mapper.query(SudokuPuzzles.class,
            // queryExpression_lessThan);
            //puzzles = mapper.query(SudokuPuzzles.class, queryExpression_lessThan);
            puzzlesResultsPage = mapper.queryPage(SudokuPuzzles.class, queryExpression_lessThan);
            puzzlesInPage = puzzlesResultsPage.getCount();
            System.out.println("Puzzles received in page, < queryPage count: " + puzzlesInPage);
            result.put("lt-page-count", puzzlesInPage);
            puzzles = puzzlesResultsPage.getResults();

            if(puzzlesInPage > 0) {
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
