# cics-java-liberty-tai
CICS Java sample Trust Association Interceptor for use with Liberty

For detailed instructions see [Configuring TAI in Liberty](https://www.ibm.com/support/knowledgecenter/SSEQTP_8.5.5/com.ibm.websphere.wlp.doc/ae/twlp_sec_tai.html)

## Basic configuration

Add the following elements to server.xml

1. Add the `appSecurity-2.0` feature to `<featureManager>`:

```xml
<featureManager> 
    <feature>appSecurity-2.0</feature> 
</featureManager>
```

2. Add a library to point to the jar file containing your TAI

```xml
<library id="simpleTAI"> 
    <fileset dir="${server.config.dir}" includes="simpleTAI.jar"/> 
</library>
```

3. Add the `<trustAssociation>` element:

```xml
<trustAssociation id="myTrustAssociation" invokeForUnprotectedURI="false" 
                  failOverToAppAuthType="false">
    <interceptors id="simpleTAI" enabled="true"  
                  className=“com.ibm.cics.sample.tai.Interceptor” 
                  invokeBeforeSSO="true" invokeAfterSSO="false" libraryRef="simpleTAI"> 
        <properties prop1="value1" prop2="value2"/>

    </interceptors> 
</trustAssociation> 
```