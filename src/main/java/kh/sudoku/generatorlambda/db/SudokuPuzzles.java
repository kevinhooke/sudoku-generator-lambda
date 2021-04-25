package kh.sudoku.generatorlambda.db;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "sudoku_puzzles")
public class SudokuPuzzles {

    private String id;
    private Integer difficulty; //1=easy, 2=medium, hard=3
    private String difficultyAssessedDate;
    private String difficultyAssessdedStatus; //success, failed
    private String humanGraderVersion; // e.g. 0.5, 1,0, 1.5 etc
    private List<String> puzzle;
    
    @DynamoDBHashKey(attributeName="id")
    public String getId() {
        return this.id;
    }
    
    public void setId(String id){
        this.id = id;
    }

    @DynamoDBRangeKey(attributeName="difficulty")
    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    @DynamoDBAttribute(attributeName="difficultyAssessedDate")
    public String getDifficultyAssessedDate() {
        return difficultyAssessedDate;
    }

    public void setDifficultyAssessedDate(String difficultyAssessedDate) {
        this.difficultyAssessedDate = difficultyAssessedDate;
    }

    @DynamoDBAttribute(attributeName="difficultyAssessdedStatus")
    public String getDifficultyAssessdedStatus() {
        return difficultyAssessdedStatus;
    }

    public void setDifficultyAssessdedStatus(String difficultyAssessdedStatus) {
        this.difficultyAssessdedStatus = difficultyAssessdedStatus;
    }

    @DynamoDBAttribute(attributeName="humanGraderVersion")
    public String getHumanGraderVersion() {
        return humanGraderVersion;
    }

    public void setHumanGraderVersion(String humanGraderVersion) {
        this.humanGraderVersion = humanGraderVersion;
    }

    @DynamoDBAttribute(attributeName="puzzle")
    public List<String> getPuzzle() {
        return puzzle;
    }

    public void setPuzzle(List<String> puzzle) {
        this.puzzle = puzzle;
    }

    
}
