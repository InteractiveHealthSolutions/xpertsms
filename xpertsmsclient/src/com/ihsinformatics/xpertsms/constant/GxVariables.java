/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Variable names from Dx software
 */

package com.ihsinformatics.xpertsms.constant;

import java.util.ArrayList;
import com.ihsinformatics.xpertsms.util.StringUtil;

/**
 * @author owais.hussain@irdresearch.org
 */
public class GxVariables {
	
	public static final String SAMPLE_ID = "sampleId";
	
	public static final String TEST_STARTED_ON = "testStartedOn";
	
	public static final String TEST_ENDED_ON = "testEndedOn";
	
	public static final String MESSAGE_SENT_ON = "messageSentOn";
	
	public static final String REAGENT_LOT_ID = "reagentLotId";
	
	public static final String CARTRIDGE_EXPIRATION_DATE = "cartridgeExpirationDate";
	
	public static final String CARTRIDGE_SERIAL = "cartridgeSerial";
	
	public static final String MODULE_SERIAL = "moduleSerial";
	
	public static final String INSTRUMENT_SERIAL = "instrumentSerial";
	
	public static final String MTB_RESULT_TEXT = "mtbResultText";
	
	public static final String RIF_RESULT_TEXT = "rifResultText";
	
	public static final String HOST_ID = "hostId";
	
	public static final String COMPUTER_NAME = "computerName";
	
	public static final String NOTES = "notes";
	
	public static final String ERROR_CODE = "errorCode";
	
	public static final String ERRORNOTES = "errorNotes";
	
	public static final String PROBEA = "probeA";
	
	public static final String PROBEB = "probeB";
	
	public static final String PROBEC = "probeC";
	
	public static final String PROBED = "probeD";
	
	public static final String PROBEE = "probeE";
	
	public static final String PROBE_SPC = "probeSpc";
	
	public static final String QC1 = "qc1";
	
	public static final String QC2 = "qc2";
	
	public static final String PROBEA_CT = "probeACt";
	
	public static final String PROBEB_CT = "probeBCt";
	
	public static final String PROBEC_CT = "probeCCt";
	
	public static final String PROBED_CT = "probeDCt";
	
	public static final String PROBEE_CT = "probeECt";
	
	public static final String PROBE_SPC_CT = "probeSpcCt";
	
	public static final String QC1_CT = "qc1Ct";
	
	public static final String QC2_CT = "qc2Ct";
	
	public static final String PROBEA_ENDPT = "probeAEndpt";
	
	public static final String PROBEB_ENDPT = "probeBEndpt";
	
	public static final String PROBEC_ENDPT = "probeCEndpt";
	
	public static final String PROBED_ENDPT = "probeDEndpt";
	
	public static final String PROBEE_ENDPT = "probeEEndpt";
	
	public static final String PROBE_SPC_ENDPT = "probeSpcEndpt";
	
	public static final String QC1_ENDPT = "qc1Endpt";
	
	public static final String QC2_ENDPT = "qc2Endpt";
	
	public static final ArrayList<String> VARIABLES = StringUtil.toArrayList(new String[] { SAMPLE_ID, TEST_STARTED_ON,
	        TEST_ENDED_ON, MESSAGE_SENT_ON, REAGENT_LOT_ID, CARTRIDGE_EXPIRATION_DATE, CARTRIDGE_SERIAL, MODULE_SERIAL,
	        INSTRUMENT_SERIAL, MTB_RESULT_TEXT, RIF_RESULT_TEXT, HOST_ID, COMPUTER_NAME, NOTES, ERROR_CODE, ERRORNOTES,
	        PROBEA, PROBEB, PROBEC, PROBED, PROBEE, PROBE_SPC, QC1, QC2, PROBEA_CT, PROBEB_CT, PROBEC_CT, PROBED_CT,
	        PROBEE_CT, PROBE_SPC_CT, QC1_CT, QC2_CT, PROBEA_ENDPT, PROBEB_ENDPT, PROBEC_ENDPT, PROBED_ENDPT, PROBEE_ENDPT,
	        PROBE_SPC_ENDPT, QC1_ENDPT, QC2_ENDPT });
}
