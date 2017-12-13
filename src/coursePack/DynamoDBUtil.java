package coursePack;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DynamoDBUtil
{
private static AmazonDynamoDB awsClient=null;	//a reusable AWS client
private static DynamoDBMapper mapper=null;		//a reusable DynamoDBMapper

/**
 * This method provides a handy way to get a <a href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/dynamodbv2/datamodeling/DynamoDBMapper.html">DynamoDBMapper</a> object.
 * The same object is reused in different requests.
 * 
 * @param regionCode		The AWS region to connect to.
 * 						Valid values are defines in <a href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/regions/Regions.html">com.amazonaws.regions.Regions</a>.
 * 						If you want to connect to a local server, use <code>null</code>.
 * @return	A DynamoDBMapper object for accessing DynamoDB.
 */
public static DynamoDBMapper getMapper(Regions regionCode)
{
if (DynamoDBUtil.mapper!=null)	//already has one
	return DynamoDBUtil.mapper;	//just return it to reuse

if (DynamoDBUtil.awsClient==null)	//no AWS client yet
	{
	AWSCredentials credentials = new DefaultAWSCredentialsProviderChain().getCredentials();	//create credentials
	DynamoDBUtil.awsClient=new AmazonDynamoDBClient(credentials);								//create AWS client
	if (regionCode==null)	//if no region is specified
		DynamoDBUtil.awsClient.setEndpoint("http://localhost:8000");	//use local server
	else	{	//else set region of client
		Region region = Region.getRegion(regionCode);
		DynamoDBUtil.awsClient.setRegion(region);
		}
	}

DynamoDBUtil.mapper=new DynamoDBMapper(DynamoDBUtil.awsClient);	//create DynamoDBMapper
return DynamoDBUtil.mapper;
} //end method
} //end class
