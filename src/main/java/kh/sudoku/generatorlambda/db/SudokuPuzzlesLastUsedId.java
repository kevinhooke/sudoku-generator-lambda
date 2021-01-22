package kh.sudoku.generatorlambda.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "SudokuPuzzlesLastUsedIds")
public class SudokuPuzzlesLastUsedId {

    private Integer id;

    @DynamoDBHashKey(attributeName="id")
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id){
        this.id = id;
    }

    
}
