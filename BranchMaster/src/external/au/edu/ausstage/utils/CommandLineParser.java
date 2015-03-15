/*
 * This file is part of the AusStage Utilities Package
 *
 * The AusStage Utilities Package is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Utilities Package is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Utilities Package.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package external.au.edu.ausstage.utils;

// import additional packages
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class of methods useful when reading arguments from the command line
 */
public class CommandLineParser {

  // declare private class level variables
  private HashMap<String,String> arguments;

  /**
   * Class constructor
   * Parse the command line arguments such that any argument starting with '-' or '--' is assumed to be a key
   * The following argument if it doesn't start with a '-' or '--' is assumed to be a value
   *
   * @param args the array of command line arguments
   */
  public CommandLineParser(String[] args) {
  
    // initialise the arguments map
    arguments = new HashMap<String, String>();
    
    // declare other helper variables
    String key   = null;
    String value = null;
    int    index = 0;
    
    // loop through the list of arguments
    for(int i = 0; i < args.length; i++) {
    
      // look for a key
      if (args[i].startsWith("--")) {
        // this is a key that starts with a --
        key = args[i].substring(2);
      } else if(args[i].startsWith("-")) {
        // this is a key that start with a -
        key = args[i].substring(1);
      } else {
        // this is a key that starts with nothing as a value
        arguments.put(args[i], null);
        
        // end this iteration of the loop
        continue;
      }
      
      // look for a value
      // does the key contain an = character?
      index = key.indexOf('=');
      
      if(index == -1) {
        // no - use the next argument as the value
        // is there a value to use
        if((i + 1) < args.length) {
          // yes - but does the value look like a key?
          if(args[i + 1].charAt(0) != '-') {
            // no - so add the key and value
            arguments.put(key, args[i + 1]);
            
            // increment the count so we don't process this value again
            i++;
          } else {
            // yes - so add just the key
            arguments.put(key, args[i]);
          }
        } else {
          // no - so just add the key
          arguments.put(key, args[i]);
        }        
          } else {
            // yes - extract the value from the key
        value = key.substring(index + 1);
        
        // fix the key
            key = key.substring(0, index);
            
            // add the key and value to the map
            arguments.put(key, value);
          }
    } // end loop  
  } // end class constructor
  
  /**
   * get the value of the first key found in the list of arguments
   * 
   * @param  key   the key to lookup
   * @return value the value of the arguments
   */
  public String getValue(String key) {
  
    // check to ensure the key is valid
    if(InputUtils.isValid(key)) {    
      // return the key if found or null if it isn't
      return arguments.get(key);    
    }
    
    // invalid key so return null
    return null;
  } // end getValue method

  
  public Set<String> getKeySet() {	  
	    return arguments.keySet();
	  }
  
  /**
   * check to see if a key exists in the list arguments
   *
   * @param  key   the key to lookup
   * @return value the value of the arguments
   */
  public boolean containsKey(String key) {
  
    // check to ensure the key is valid
    if(InputUtils.isValid(key)) {
    
      if(arguments.get(key) != null) {
        return true;
      } else {
        return false;
      }  
    }
    
    // invalid key so return null
    return false;
  
  } // end containsKey method
  
} // end class definition

class InputUtils {

  /**
   * check to ensure a parameter value is valid
   *
   * @param value the parameter value
   *
   * @return      true if, and only if, the parameter is valid
   */
  public static boolean isValid(String value) {
  
    // check on the parameter value
    if(value == null) {
      return false;
    } else {
      value = value.trim();
      if(value.equals("") == true) {
        return false;
      } 
    }
    
    // passed validation
    return true;
  } // end the isValid method
  
  /**
   * check to ensure a parameter is valid and is from a list
   *
   * @param value the parameter value
   * @param list  the list of allowed values
   *
   * @return      true if, and only if, the parameter is valid
   */
  public static boolean isValid(String value, String[] list) {
  
    // check if it is not null first
    if(isValid(value) == false) {
      return false;
    }
    
    // check the value against the list
    boolean isValid = false;
    
    for(int i = 0; i < list.length; i++) {
      if(list[i].equals(value)) {
        isValid = true;
      }
    }
    
    return isValid;  
  } // end the isValid method with list
  
  /**
   * check to ensure a parameter value is valid
   *
   * @param value the parameter value
   *
   * @return      true if, and only if, the parameter is valid
   */
  public static boolean isValidInt(String value) {
  
    if(isValid(value) == false) {
      return false;
    }
  
    // can we parse the value as a int?
    try {
      Integer.parseInt(value);
    } catch (java.lang.NumberFormatException ex) {
      // nope
      return false;
    }
    
    // if we get this far everything is OK
    return true;
  
  } // end the isValid method
  
  /**
   * check to ensure a parameter value is valid
   *
   * @param value   the parameter value
   * @param minimum the minimum allowed value
   *
   * @return      true if, and only if, the parameter is valid
   */
  public static boolean isValidInt(int value, int minimum) {
    
    if(value >= minimum) {
      return false;
    } else {
      return true;
    }
      
  } // end the isValid method
  
  /**
   * check to ensure a parameter value is valid
   *
   * @param value    the parameter value
   * @param minimum  the minimum allowed value
   * @param maximum the maximum allowed value
   *
   * @return      true if, and only if, the parameter is valid
   */
  public static boolean isValidInt(int value, int minimum, int maximum) {
    
    if(value >= minimum && value <= maximum) {
      return true;
    } else {
      return false;
    }      
  } // end the isValid method
  
  /**
   * check to ensure a parameters value is valid
   *
   * @param date the date to check in the format yyyy-mm-dd
   */
  public static boolean isValidDate(String date) {
  
    // define the pattern
    Pattern pattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    Matcher matcher = pattern.matcher(date);
    
    if(matcher.find() == true) {
      return true;
    } else {
      return false;
    }  
  }
    
  
  /**
   * A method to take an array of values and return them in a comma delimited list
   * 
   * @param values the array of values to process
   *
   * @return       the string representation of the list
   */
  public static String arrayToString(String[] values) {
  
    return java.util.Arrays.toString(values).replaceAll("[\\]\\[]", "");
  
  } // end the arrayToString method
  
  /**
   * A method to ensure an array of strings represent an array of integers
   *
   * @param values the array of strings
   *
   * @return       true if, and only if, all of the values pass inspection
   */
  public static boolean isValidArrayInt(String[] values) {
  
    // declare helper variables
    boolean status = true;
    
    // loop through all of the elements in the array
    for(int i = 0; i < values.length; i++) {
      if(isValidInt(values[i]) == false) {
        // this value isn't valid
        status = false;
        
        // exit the loop early
        i = values.length;
      }
    }
    
    // return the status
    return status;  
  
  } // end isValidArrayInt method
  
} // end class definition
