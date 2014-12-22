=== XpertSMS ===
Contributors: IHS
Software Type: Free, Open-source
Requires: Microsoft Windows 7 or higher, Oracle Java Runtime Environment (JRE) v6.0 or higher, GeneXpert Dx Software 3.0 or higher
License: GPLv3

== Description ==
The software program connects with GeneXpert Dx software and uses various channels to post the GeneXpert test results to multiple targets

== Installation ==
1. Download and install Oracle JDK v6.0 or higher
2. That's it. Double click the xpertsmsclient-x.x.x.jar file to run

== Configuration ==
Since XpertSMS sends GeneXpert results through multiple channels, you may have to configure multiple targets as per requirement
1. SMS
...

2. OpenMRS
OpenMRS is a famous open-source medical record system, which can be integrated with many other systems.
XpertSMS requires that you have compatible REST module installed in the instance of OpenMRS that will be used. Here is how to configure it:
- OpenMRS REST-WS Address is the URI to post/get data to/from OpenMRS
- OpenMRS usernamd and password are credentials to the OpenMRS. It is highly recommended that you create a dedicated user account with a custom role.
The user must have credentials to: create/edit patients, concepts, encounter types, encounters and observations
- Date/Time format should be kept as default. This specifies in which format will the data be posted to OpenMRS via REST
- Encounter Type is the name of encounter type in the OpenMRS that will be used to post GeneXpert results. XpertSMS creates one if it doesn't exist
- SSL/TLS Encryption option enables the data to be sent via a secure channel. This option requires you to attach a SSL certificate
- Concept mapping associates XpertSMS variables with the OpenMRS concepts. Please carefully look at the sample and write your mappings accordingly.
Although, keeping it as is is highly recommended. If new concepts need to be created, the XpertSMS will do so automatically

3. GXAlert
GXAlert is a reporting system for GeneXpert results. It is recommended for extensive reporting. Configure as directed:
- Set the Server's address. This will be where you have deployed the GXAlert. The default settings will post your results to the GXAlert's public server
- Use port number, if required
- GXAlert API code is provided by the GXAlert team. This works as authentication channel
- Date/Time format is the format in which GXAlert accepts date and time. Keep it default unless advised otherwise by GXAlert implementers
- SSL/TLS Encryption option enables the data to be sent via a secure channel. This option requires you to attach a SSL certificate

4. CSV
Configured when data is to be collected on local computer:
- Set folder path where the CSV files will be generated
- Field separator defines how the columns will be separated
- Check use quotes when text fields are to be enclosed in quotations
- Select variables (keep Ctrl pressed to select multiple variables) from GeneXpert results to be shown in CSV file
- Press "Try.." button to check if the configuration is correct. Finallly, Save to save your settings
