
package csi403;
import java.io.*;
import java.io.PrintWriter;
import java.util.*;
import javax.json.*;
import javax.servlet.*;
import javax.servlet.http.*;
// Import required java libraries
// Extend HttpServlet class
public class priorityQueue extends HttpServlet {
	// Standard servlet method
	public void init() throws ServletException
	{
		// Do any required initialization here - likely none
	}
	// Standard servlet method - we will handle a POST operation
	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException
	{
		doService(request, response);
	}
	// Standard servlet method - we will not respond to GET
	public void doGet(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException
	{
		// Set response content type and return an error message
		// response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.println("{ 'message' : 'Use POST!'}");
	}
	// Our main worker method
	// Parses messages e.g. {"inList" : [5, 32, 3, 12]}
	// Returns the list reversed.
	private void doService(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException
	{
		// Get received JSON data from HTTP request
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String jsonStr = "";
		if(br != null){
			jsonStr = br.readLine();
		}
		try {
			// Create JsonReader object
			StringReader strReader = new StringReader(jsonStr);
			JsonReader reader = Json.createReader(strReader);
			// Get the singular JSON object (name:value pair) in this message.
			JsonObject obj = reader.readObject();
			// From the object get the array named "inList"
			JsonArray inArray = obj.getJsonArray("inList");
			ArrayList<JsonObject> queueList = new ArrayList<JsonObject>();
			boolean flag = false;
			if(inArray.getJsonObject(0).getInt("pri") < 0) {								// Negative test for the first JsonObject in the array
				flag = true;
			}
			for (int i = 0; i < inArray.size(); i++) {
				JsonObject thing = inArray.getJsonObject(i);
				if ("enqueue".equals(thing.getString("cmd"))) {
					for (int j = 0; j < queueList.size(); j++) {   
						if(thing.getInt("pri") < 0) {										// Negative for the rest of the array
						flag = true;
					}	else if(thing.getInt("pri") <= queueList.get(j).getInt("pri")) {
							queueList.add(j, thing);
							break;
						} 
					}
					if (!queueList.contains(thing)) {
						queueList.add(thing);
					}
				} 
				else if ("dequeue".equals(thing.getString("cmd"))) {
					queueList.remove(0);
				}
			}
			JsonArrayBuilder outArrayBuilder = Json.createArrayBuilder();
			// 
			// converts java sting array to JSON array
			for (int y=0; y < queueList.size();y++){
				outArrayBuilder.add(queueList.get(y).getString("name"));
			}

			if(flag == false) {
				PrintWriter cout = response.getWriter();
				cout.println("{ \"outList\" : " + outArrayBuilder.build().toString() + "}"); 
			}else if(flag == true) {
				PrintWriter cout = response.getWriter();
				cout.println("{ \"message\" : " + "Malformed JSON" + "}");
			}

		}
		catch (javax.json.stream.JsonParsingException e){
			PrintWriter cout = response.getWriter();
			cout.println("{ \"message\" : " + "Malformed JSON" + "}");
		}
		catch(NullPointerException e){
			PrintWriter cout = response.getWriter();
			cout.println("{ \"message\" : " + "Can't find inList" + "}");
		}
		catch(ClassCastException e){
			PrintWriter cout = response.getWriter();
			cout.println("{ \"message\" : " + "Values can't be sorted" + "}");
		}
		catch (javax.json.JsonException e) {
			PrintWriter cout = response.getWriter();
			cout.println("{ \"message\" : " + "Cannot read JSON object" + "}");
		}
	}
	// Standard Servlet method
	public void destroy(){
	}
}
