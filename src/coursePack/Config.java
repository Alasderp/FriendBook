package coursePack;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.Region;

public class Config
{
//
// table name in DynamoDB
//
public static final String DYNAMODB_TABLE_NAME="cmm529-cw-user";

public static final String DYNAMODB_TABLE_NAME_SUBREQ="cmm529-cw-request";

public static final String DYNAMODB_TABLE_NAME_SUB="cmm529-cw-subscription";

//
// AWS Region. Refer to API to see what regions are available.
// *** To use a local server, set this to null. ***

//USED FOR LOCAL PURPOSES
//public static final Regions AWS_REGION=null;

public static final Regions AWS_REGION=Regions.US_WEST_2;
} //end class
