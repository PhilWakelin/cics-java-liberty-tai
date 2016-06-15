# cics-java-liberty-tai
CICS Java sample Trust Association Interceptor for use with Liberty

For detailed instructions see [Configuring TAI in Liberty](https://www.ibm.com/support/knowledgecenter/SSEQTP_8.5.5/com.ibm.websphere.wlp.doc/ae/twlp_sec_tai.html) and [Developing a custom TAI for Liberty](https://www.ibm.com/support/knowledgecenter/en/SSEQTP_8.5.5/com.ibm.websphere.wlp.doc/ae/twlp_dev_custom_tai.html)

## Introduction

You can configure Liberty to integrate with a third-party security service by using Trust Association Interceptors (TAI). The TAI can be called before or after single sign-on (SSO). You can develop a custom trust association interceptor (TAI) class by implementing the com.ibm.wsspi.security.tai.TrustAssociationInterceptor interface provided in the Liberty server.

The trust association interface is a service provider API that enables the integration of third-party security services with a Liberty server. When processing the web request, the Liberty server calls out and passes the `HttpServletRequest` and `HttpServletResponse` to the trust association interceptors. The `HttpServletRequest` calls the `isTargetInterceptor` method of the interceptor to see whether the interceptor can process the request. After an appropriate trust association interceptor is selected, the `HttpServletRequest` is processed by the `negotiateValidateandEstablishTrust` method of the interceptor, and the result is returned in a `TAIResult` object. You can add your own logic code to each method of the custom TAI class.

> Note: The use of Trust Association Interceptors should be handled with care. Where possible use a standard supported mechanism within Liberty to achieve security architecture and integration goals.

The sample TAI takes a `userid` value passed in the header of the `HttpServletRequest`. This userid could have been propagated by an external authentication mechanism, and in this example the value of this header attribute will be used as the SAF user ID that the transaction will run under.

Although this sample shows a basic use of a TAI, within the TAI `negotiateValidateandEstablishTrust` method you can implement your own more advanced authentication methods, for example calling a third-party security application.

## Create a JAR

Once you have adapted the sample TAI to suit your needs you will need to export the class as part of a jar file.

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