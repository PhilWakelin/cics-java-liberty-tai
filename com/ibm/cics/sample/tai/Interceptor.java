/* Licensed Materials - Property of IBM                                   */
/*                                                                        */
/* SAMPLE                                                                 */
/*                                                                        */
/* (c) Copyright IBM Corp. 2016 All Rights Reserved                       */
/*                                                                        */
/* US Government Users Restricted Rights - Use, duplication or disclosure */
/* restricted by GSA ADP Schedule Contract with IBM Corp                  */
/*                                                                        */

package com.ibm.cics.sample.tai;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.cics.server.Task;
import com.ibm.websphere.security.WebTrustAssociationException;
import com.ibm.websphere.security.WebTrustAssociationFailedException;
import com.ibm.wsspi.security.tai.TAIResult;
import com.ibm.wsspi.security.tai.TrustAssociationInterceptor;

public class Interceptor implements TrustAssociationInterceptor {

	@Override
	public void cleanup() {
		// Do nothing
	}

	@Override
	public String getType() {
		return "TAI Inteceptor " + this.getVersion();
	}

	@Override
	public String getVersion() {
		return "TAI v1";
	}

	@Override
	public int initialize(Properties props)
			throws WebTrustAssociationFailedException {

		// Start up message
		printMsg("Initialising TAI Intercept class " + this.getType());
		
		return 0;
	}

	@Override
	public boolean isTargetInterceptor(HttpServletRequest request)
			throws WebTrustAssociationException {
	
		return true;
		
	}

	@Override
	public TAIResult negotiateValidateandEstablishTrust(
			HttpServletRequest request, HttpServletResponse arg1)
			throws WebTrustAssociationFailedException {
		
		// set user ID from query string
		String userid = request.getHeader("userid");
		
		// if no user id supplied return unauthorized
		if (userid == null)
		    return TAIResult.create(HttpServletResponse.SC_UNAUTHORIZED);
			
		try {
			// the code below shows an example of how you can use JCICS within your
			// TAI. At this point you could call out to an external existing security
			// module, if this is applicable.
			
			// For this sample we will print the current user ID and transaction name for
			// the task we are running under.
			
			Task task = Task.getTask();
			if (task == null) {
				// Cannot continue as we have no access to JCICS.
				
				printMsg("TAI - No access to JCICS");
				
				return TAIResult.create(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} else {		
				String msg = "Task("+task.getTaskNumber() + ") " +
                      "Tranid("+task.getTransactionName() +") " +
                      "running under CICS User ID (" + task.getUSERID() +")" ;
				
				printMsg("TAI - " + msg);
            }
		} catch (Exception e) {
			// If we hit an exception we have encountered a problem with JCICS, so we
			// return an internal server error
			
			return TAIResult.create(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
	
		printMsg ("TAI - Establishing trust to user(" + userid + ")");
		
		// Return OK return code and user ID to create subject.
		// The subject will be used as the user ID that the transaction will run under.
		
		return TAIResult.create(HttpServletResponse.SC_OK, userid);
	}
	
	public static String formatTime() {
		Date timestamp = new Date();
		SimpleDateFormat dfTime = null;
		dfTime = (SimpleDateFormat) DateFormat.getTimeInstance();
		dfTime.applyPattern("dd/MM/yy HH:mm:ss:SSS");
		String time = dfTime.format(timestamp);
		return time;
	}
	
	public static void printMsg (String msg) {
		System.out.println(formatTime() + " " + msg );
		System.out.flush();
	}



}
