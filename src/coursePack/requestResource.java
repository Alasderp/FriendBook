package coursePack;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;

import cmm529.coursework.friend.model.*;
import cmm529.coursework.friend.*;

@Path("/requestResource")
public class requestResource {
	
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public User getUser(@PathParam("id") String id)
	{
		User user = null;
		DynamoDBMapper mapper=DynamoDBUtil.getMapper(Config.AWS_REGION);
		user=mapper.load(User.class,id);
	
		//CREATE A NEW USER HERE IF NOT EXISTING IN DATABASE???
		if (user==null){
			//throw new WebApplicationException(404);
			return new User(id, new Location(0, 0), 0);
		}
		else{
			return user;
		}
		
	}

	@Path("/requests/all")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<SubscriptionRequest> getAllRequests()
	{
		try{
			DynamoDBMapper mapper=DynamoDBUtil.getMapper(Config.AWS_REGION);	
			DynamoDBScanExpression scanExpression=new DynamoDBScanExpression();
			List<SubscriptionRequest> result=mapper.scan(SubscriptionRequest.class, scanExpression);
			return result;
		} catch (Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	} //end method
	
	@Path("/subscriptions/all")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Subscription> getAllSubscriptions()
	{
		try{
			DynamoDBMapper mapper=DynamoDBUtil.getMapper(Config.AWS_REGION);	
			DynamoDBScanExpression scanExpression=new DynamoDBScanExpression();
			List<Subscription> result=mapper.scan(Subscription.class, scanExpression);
			return result;
		} catch (Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	} //end method
	
	@Path("/approve")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response acceptRequest(@FormParam("subscribeTo") String subscribeTo,
								 @FormParam("subscriber") String subscriber){
		
		System.out.println(subscriber + " Subscribing to: " + subscribeTo);
		
		DynamoDBMapper mapper=DynamoDBUtil.getMapper(Config.AWS_REGION);
		
		Subscription subscriptions=mapper.load(Subscription.class, subscriber);
			
		Set<String> temp = new HashSet<>();
		if(subscriptions != null){
			temp.addAll(subscriptions.getSubscribeTo());
		}
		else{
			subscriptions = new Subscription(subscriber, temp);
		}

		temp.add(subscribeTo);
		subscriptions.setSubscribeTo(temp);
		
		try	{			
			mapper=DynamoDBUtil.getMapper(Config.AWS_REGION);
			mapper.save(subscriptions);
			
			SubscriptionRequest request = new SubscriptionRequest(subscriber, subscribeTo, 0);
			
			mapper.delete(request);
			
			return Response.status(201).entity("request saved").build();
			} catch (Exception e)
				{
				System.out.println(e.getMessage());
				return Response.status(400).entity("error in saving request").build();
				}
	}
	
	@Path("/addUser")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response addAUser(	@FormParam("id") String id,
								@FormParam("latitude") double latitude,
								@FormParam("longitude") double longitude,
								@FormParam("lastUpdated") long lastUpdated)
	{
	try	{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		User user = new User(id, new Location(longitude, latitude), timestamp.getTime());
		
		DynamoDBMapper mapper=DynamoDBUtil.getMapper(Config.AWS_REGION);
		mapper.save(user);
		return Response.status(201).entity("user saved").build();
		} catch (Exception e)
			{
			return Response.status(400).entity("error in saving user").build();
			}
	} //end method
	
	
	@Path("/sendRequest")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response saveARequest(	@FormParam("subscribeTo") String subscribeTo,
								@FormParam("subscriber") String subscriber)
	{
	try	{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		//System.out.println("Subscriber: " + subscriber + " To: " + subscribeTo + " At: " + this.getTimeStamp());
		
		SubscriptionRequest request=new SubscriptionRequest(subscriber, subscribeTo, timestamp.getTime());
		
		DynamoDBMapper mapper=DynamoDBUtil.getMapper(Config.AWS_REGION);
		mapper.save(request);
		return Response.status(201).entity("request saved").build();
		} catch (Exception e)
			{
			System.out.println(e.getMessage());
			return Response.status(400).entity("error in saving request").build();
			}
	} //end method
	
	@Path("/deny/{subscribeTo}/{subscriber}")
	@DELETE
	public Response deleteRequest(@PathParam("subscribeTo") String subscribeTo, @PathParam("subscriber") String subscriber)
	{
	System.out.println("Hi: " + subscribeTo + " Deleting request from: " + subscriber);
	DynamoDBMapper mapper=DynamoDBUtil.getMapper(Config.AWS_REGION);
	SubscriptionRequest request=mapper.load(SubscriptionRequest.class,subscribeTo, subscriber);

	if (request==null)
		throw new WebApplicationException(400);

	mapper.delete(request);
	return Response.status(200).entity("deleted").build();
	} //end method
	
}
