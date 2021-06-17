package kh.sudoku.generatorlambda;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import kh.sudoku.PuzzleResults;
import kh.sudoku.generator.GeneratedPuzzleWithDifficulty;
import kh.sudoku.generator.SudokuGenerator;
import kh.sudoku.generatorlambda.db.SudokuPuzzles;

/**
 * Lambda for the Bulk Lambda Generator. Generates requested number of valid puzzles
 * and writes to DynamoDB.
 * 
 * Lambda parameters:
 * 
 * targetGivens : number of givens required in the generated puzzles. Min value: 17, max (right
 * now until bug fixed is 20)
 * 
 * puzzles: number of puzzles to generate
 * 
 * @author kev
 *
 */
public class SudokuBulkGeneratorHandler implements RequestHandler<Map<String, String>, String> {

    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    
    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        String result = "failed";
        int validPuzzlesGenerated = 0;

        System.out.println("targetGivens: " + event.get("targetGivens"));
        System.out.println("puzzles: " + event.get("puzzles"));

        
        SudokuGenerator generator = new SudokuGenerator();
        
        // TODO: parameterize this
        
        List<GeneratedPuzzleWithDifficulty> results = generator.generateGradedPuzzles(20, 1);
        for (GeneratedPuzzleWithDifficulty puzzle : results) {
            List<String> generatedShorthand = puzzle.getResults().getResults().get(0);
            for (String shorthand : generatedShorthand) {
                System.out.println(shorthand);
            }

            SudokuPuzzles puzzleToStore = new SudokuPuzzles();
            String dateNow = this.getFormattedISODate();
            puzzleToStore.setDifficultyAssessedDate(dateNow);
            puzzleToStore.setGeneratedDate(dateNow);
            String difficulty = puzzle.getDifficulty().getDifficulty().toString();
            System.out.println("Difficulty: " + difficulty);
            puzzleToStore.setDifficulty(difficulty);
            puzzleToStore.setPuzzle(generatedShorthand);
            DynamoDBMapper mapper = new DynamoDBMapper(client);
            mapper.save(puzzleToStore);
        }
        return "Successful puzzles generated: " + validPuzzlesGenerated;
    }

    String getFormattedISODate() {
        return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
    }
}
