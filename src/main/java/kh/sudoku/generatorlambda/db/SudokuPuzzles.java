package kh.sudoku.generatorlambda.db;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "sudoku_puzzles")
public class SudokuPuzzles {

    private String id;
    private String difficulty; //easy, medium, hard
    private Integer givens;
    private String difficultyAssessedDate;
    private String difficultyAssessedStatus; //success, failed
    private String generatedDate;
    private String humanGraderVersion; // e.g. 0.5, 1,0, 1.5 etc
    private List<String> puzzle;
    
    @DynamoDBAutoGeneratedKey
    @DynamoDBHashKey(attributeName="id")
    public String getId() {
        return this.id;
    }
    
    public void setId(String id){
        this.id = id;
    }

    @DynamoDBRangeKey(attributeName="difficulty")
    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    @DynamoDBAttribute(attributeName="difficultyAssessedDate")
    public String getDifficultyAssessedDate() {
        return difficultyAssessedDate;
    }

    public void setDifficultyAssessedDate(String difficultyAssessedDate) {
        this.difficultyAssessedDate = difficultyAssessedDate;
    }

    @DynamoDBAttribute(attributeName="difficultyAssessedStatus")
    public String getDifficultyAssessdedStatus() {
        return difficultyAssessedStatus;
    }

    public void setDifficultyAssessdedStatus(String difficultyAssessdedStatus) {
        this.difficultyAssessedStatus = difficultyAssessdedStatus;
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

    @DynamoDBAttribute(attributeName="givens")
    public Integer getGivens() {
        return givens;
    }

    public void setGivens(Integer givens) {
        this.givens = givens;
    }

    @DynamoDBAttribute(attributeName="generatedDate")
    public String getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(String generatedDate) {
        this.generatedDate = generatedDate;
    }

    
}