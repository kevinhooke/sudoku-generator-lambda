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
import kh.sudoku.generator.SudokuGenerator;
import kh.sudoku.generatorlambda.db.SudokuPuzzles;


public class SudokuBulkGeneratorHandler implements RequestHandler<Map<String,String>, String>{

    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();

    
    @Override
    public String handleRequest(Map<String,String> event, Context context)
    {
        String result = "failed";
        int validPuzzlesGenerated = 0;
        
        SudokuGenerator generator = new SudokuGenerator();
        //TODO: parameterize this
        for(int puzzles=0; puzzles < 10; puzzles++) {
            for(int attemptsForValidPuzzle = 0; attemptsForValidPuzzle < 5; attemptsForValidPuzzle++) {
    
                PuzzleResults results = generator.generate(60);
                if(results.isValidPuzzle()) {
                    List<List<String>> generatedPuzzles = results.getResults();
                    for (List<String> shorthand : generatedPuzzles) {
                        System.out.println(shorthand);
                        SudokuPuzzles puzzle = new SudokuPuzzles();
                        //TODO: change this to ISO string later
                        puzzle.setId(this.getFormattedISODate());
                        // 0 = unrated so far, until grader runs  
                        puzzle.setDifficulty(0);
                        puzzle.setPuzzle(shorthand);
                        DynamoDBMapper mapper = new DynamoDBMapper(client);
                        mapper.save(puzzle);
                    }
                    validPuzzlesGenerated++;
                    break;
                }
            }
        }
        return "Successful puzzles generated: " + validPuzzlesGenerated;
    }
    
    String getFormattedISODate() {
        return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
    }
}
