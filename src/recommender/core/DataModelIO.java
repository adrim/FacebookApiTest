/**
 * 
 */
package recommender.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;

/**
 * @author Adriana
 *
 */
public class DataModelIO {
	public DataModel readModelFromFile(String databaseFile) {
		 DataModel dataModel = null;
		 try {
			 dataModel = new FileDataModel(new File(databaseFile));
		 } catch (IOException e) {
			 System.err.println("\n\tFATAL: could not initialize model\n");
			 e.printStackTrace();
			 System.exit(0);
		 }
		 return dataModel;
	}
	/**
	 * Read the mapping from Facebook id to local id
	 * Reason for new id: the recommender uses Long IDs, not String
	 * @return userMappings
	 */
	public HashMap<String, Long> readUserMappingsFromFile(String userMappingsFile) {
		Scanner 			  sc		   = new Scanner(userMappingsFile);
		HashMap<String, Long> userMappings = new HashMap<String, Long>();
		
		while (sc.hasNext()) {
			userMappings.put(sc.next(), sc.nextLong());
		}
		sc.close();
		
		return userMappings;
	}
	/**
	 * Write Data Model to file to be uploaded when the server goes up
	 */
	public void writeModelToFile(FastByIDMap<PreferenceArray> userData,
								 String databaseFile) {
		try {
			PrintWriter pr = new PrintWriter(databaseFile);
			for (Entry<Long, PreferenceArray> userPref  : userData.entrySet()) {
				Long id = userPref.getKey();
				PreferenceArray prefs = userPref.getValue();
				for (Preference pref : prefs) {
					System.out.println("to string pref  " + pref.toString());
					pr.println(id + "," + pref.getItemID() + ", " + pref.getValue());
				}
			}
			pr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void writeUserMappingsToFile(HashMap<String, Long> userMappings,
										String userMappingsFile) {
		try {
			PrintWriter pw = new PrintWriter(userMappingsFile);
			for (Entry<String, Long> mapping : userMappings.entrySet()) {
				pw.println(mapping.toString());
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Initialize the recommender
	 * @param file
	 */
	
}
