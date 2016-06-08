# cics-java-liberty-tai
CICS Java sample Trust Association Interceptor for use with Liberty

For detailed instructions see [Configuring TAI in Liberty](https://www.ibm.com/support/knowledgecenter/SSEQTP_8.5.5/com.ibm.websphere.wlp.doc/ae/twlp_sec_tai.html)

## Introduction

The sample Interceptor takes a userid passed in the header of the HTTP request. This userid could have been propagated by an external authentication mechanism, and in this example the value of this header attribute will be used as the SAF user ID that the transaction will run under.

Although this sample shows a basic use of a TAI, within the TAI negotiateValidateandEstablishTrust(…) method you can implement your own more advanced authentication methods, for example using the JCICS API to call an authentication module in a CICS Program.

## Create a JAR

Once you have adapted the sample Interceptor to suit your needs you will need to export the class as part of a jar file.

Within eclipse / CICS Explorer (or other mechanism), you can do this by right-clicking on the containing project and clicking Export… You will only need to export the Interceptor.class file. In the example configuration below we call this jar simpleTAI.jar.

Once you have created the JAR file, upload this (as binary) to zFS. In the example configuration we put this in the server configuration directory (same directory as server.xml).

## Basic configuration

The basic configuration requires changes to server.xml. Add the following elements to server.xml:

Add the `appSecurity-2.0` feature to `<featureManager>`:

```xml
<featureManager> 
    <feature>appSecurity-2.0</feature> 
</featureManager>
```

Add a library to point to the jar file containing your TAI. In our example we have created a jar called simpleTAI.jar and placed it in the same directory as server.xml

```xml
<library id="simpleTAI"> 
    <fileset dir="${server.config.dir}" includes="simpleTAI.jar"/> 
</library>
```

Add the `<trustAssociation>` element:

```xml
<trustAssociation id="myTrustAssociation" invokeForUnprotectedURI="false" 
                  failOverToAppAuthType="false">
    <interceptors id="simpleTAI" enabled="true"  
                  className="com.ibm.cics.sample.tai.Interceptor" 
                  invokeBeforeSSO="true" invokeAfterSSO="false" libraryRef="simpleTAI">
    </interceptors> 
</trustAssociation> 
```

You will need to change the className attribute to match the name of your TAI class.

The id attribute in the `<interceptors>` element we define as the same value as the id for the `<library>`.

This also sets the failOverToAppAuthType attribute to false, so app security is disabled.

For explanations of the other attributes see [Configuring TAI in Liberty](https://www.ibm.com/support/knowledgecenter/SSEQTP_8.5.5/com.ibm.websphere.wlp.doc/ae/twlp_sec_tai.html)