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

        DynamoDBQueryExpression<SudokuPuzzles> queryExpression_greaterThan = new DynamoDBQueryExpression<SudokuPuzzles>()
                .withKeyConditionExpression("difficulty = :val2 and id > :val1")
                .withExpressionAttributeValues(eav)
                .withIndexName("PuzzleByDifficultyIndex")
                .withConsistentRead(false)
                .withLimit(1);

        DynamoDBQueryExpression<SudokuPuzzles> queryExpression_lessThan = new DynamoDBQueryExpression<SudokuPuzzles>()
                .withKeyConditionExpression("difficulty = :val2 and id < :val1")
                .withExpressionAttributeValues(eav)
                .withIndexName("PuzzleByDifficultyIndex")
                .withConsistentRead(false)
                .withLimit(1);
        
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

        String message = null;
        if (puzzles.size() > 0) {
            result.put("puzzlesInList", puzzles.size());
            result.put("puzzle", puzzles.get(0));
            message = "success";
        } else {
            result.put("puzzle", new ArrayList<String>());
            message = "no_results";
        }

        Response responseBody = new Response(message, result);
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Credentials", "true");
        headers.put("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        headers.put("Access-Control-Allow-Headers",
          "x-www-form-urlencoded, Origin, X-Requested-With, Content-Type, Accept, Authorization");
        headers.put("Content-Type", "application/json");
        return ApiGatewayResponse
                .builder()
                .setStatusCode(200)
                .setHeaders(headers)
                .setObjectBody(responseBody).build();
    }
}
