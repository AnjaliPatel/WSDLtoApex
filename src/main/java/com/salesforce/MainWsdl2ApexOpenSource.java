package com.salesforce.ide.wsdl2apex.core;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class MainWsdl2ApexOpenSource {

	private static ArrayList<String> allClassNames;
	private static ArrayList<String> allClasses;
	
	public ArrayList getAllClassNames()
	{
		return allClassNames;
	}
	
	public ArrayList getAllClasses()
	{
		return allClasses;
	}
	
	public static void parseAndGenerate(String[] args) throws RuntimeException, CalloutException, IOException{
		String filePath = args[0];
		Boolean async;
		String resultPath = null;
		if(args.length == 2)
		{
			async = Boolean.parseBoolean(args[1]);
		}
		else if(args.length == 3)
		{
			resultPath = args[1];
			async = Boolean.parseBoolean(args[2]);
		}
		else
		{
			System.err.println("Must either contain the following 2 arguements: path to the wsdl file and whether you want an asynchronous class");
			System.err.println("or the following 3 arguements: path to the wsdl file, path to where you want to save the file (with a slash at the end), and whether you want an asynchronous class");
			throw new RuntimeException();
		}
		FileString f = new FileString();
		String wsdlString;
		Wsdl2Apex.AnalysisResult result = null;
		try
		{
			wsdlString = f.getStringFromFile(filePath);								//get the wsdl in a stirng
		}
		catch(FileNotFoundException e)
		{
			System.err.println("Cannot find the file");
			throw e;
		}
		try
		{
			result = new Wsdl2Apex().analyze(wsdlString);							//parse the wsdl
			String allMessages = new String();
			if(!result.isSuccess())
			{
				ArrayList<String> messages = new ArrayList<String>();
				messages = result.getMessages();
				for(String message : messages)
				{
					System.out.println(message);
					allMessages = allMessages + message + '\n';
				}
				throw new RuntimeException(allMessages);
			}
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			throw e;
		}
		HashMap<String, String> inputMap = new HashMap<String, String>();
		inputMap = result.getMapping();												//get the targetnamesapces
		Wsdl2Apex wsdl2Apex = new Wsdl2Apex();
		LinkedHashMap<String, APackage> packageMap;
		allClassNames = new ArrayList<String>();
		allClasses = new ArrayList<String>();
		try
		{
			packageMap = wsdl2Apex.generate(wsdlString, Wsdl2ApexOptions.newDefault(async).setPackageNamespaceMap(inputMap)); //generates the class
			allClasses = wsdl2Apex.generateApexClass(packageMap);				//creates the class as a string
			Iterator<String> j = packageMap.keySet().iterator();
			while(j.hasNext())
			{
				allClassNames.add(j.next().toString());
			}
		}
		
		catch(CalloutException e)
		{
			System.err.println("Failed to generate code");
			System.out.println(e.getMessage());
			throw e;
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		if(args.length == 3)
		{
			try
			{
				Iterator i = allClasses.iterator();
				Iterator j = allClassNames.iterator();
				while(i.hasNext() && j.hasNext())
				{
					f.StringToFile(resultPath, i.next().toString(), j.next().toString());		//writes the class into a file
				}
			}
			catch(FileNotFoundException | UnsupportedEncodingException e)
			{
				System.err.println("Cannot find the file");
				throw e;
			}
		}
	}
	
	public static void parse(String[] args) throws IOException, CalloutException{
		String filePath = args[0];
		Boolean async;
		String resultPath = null;
		if(args.length == 2)
		{
			async = Boolean.parseBoolean(args[1]);//async or not
		}
		else if(args.length == 3)
		{
			resultPath = args[1];
			async = Boolean.parseBoolean(args[2]);
		}
		else
		{
			System.err.println("Must either contain the following 2 arguements: path to the wsdl file and whether you want an asynchronous class");
			System.err.println("or the following 3 arguements: path to the wsdl file, path to where you want to save the file (with a slash at the end), and whether you want an asynchronous class");
			throw new RuntimeException();
		}						
		FileString f = new FileString();
		String wsdlString;
		Wsdl2Apex.AnalysisResult result = null;
		try
		{
			wsdlString = f.getStringFromFile(filePath);								//get the wsdl in a stirng
		}
		catch(FileNotFoundException e)
		{
			System.err.println("Cannot find the file");
			throw e;
		}
		try
		{
			result = new Wsdl2Apex().analyze(wsdlString);							//parse the wsdl
			String allMessages = new String();
			if(!result.isSuccess())
			{
				ArrayList<String> messages = new ArrayList<String>();
				messages = result.getMessages();
				for(String message : messages)
				{
					System.out.println(message);
					allMessages = allMessages + message + '\n';
				}
				throw new RuntimeException(allMessages);
			}
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			throw e;
		}
	}
	public static void main(String[] args) throws IOException, CalloutException
	{
		parseAndGenerate(args);
	}
}

