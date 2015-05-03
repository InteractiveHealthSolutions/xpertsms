USE tbreach_dw;

-- STORED PROCEDURE TO PULL DATA FROM OLTP
DELIMITER $$
CREATE PROCEDURE tbreach_dw.ETL_process ()
BEGIN
	USE tbreach_dw;
	-- Fill dimension tables
	TRUNCATE dim_chw;
	INSERT INTO dim_chw (chwid, monitor_id, full_name, gender, phone, mobile)
		SELECT W.WorkerID AS chw_id, W.MonitorID AS monitor_id, CONCAT(P.FirstName, ' ', P.LastName) AS full_name, P.Gender, C.Phone,  C.Mobile
		FROM tbreach.Worker AS W
		LEFT OUTER JOIN tbreach.Person P ON P.PID = W.WorkerID
		LEFT OUTER JOIN tbreach.Contact C ON C.PID = W.WorkerID
		WHERE WorkerID NOT IN (SELECT distinct chwid from dim_chw);
	TRUNCATE dim_encounter;
	INSERT INTO dim_encounter (encounter_id, person_id, patient_id, encounter_type, date_entered, date_started, date_ended, element, value)
		SELECT E.EncounterID, E.PID1, E.PID2, D.definition_key, E.DateEncounterEntered, E.DateEncounterStart, E.DateEncounterEnd, ER.Element, ER.Value
		FROM tbreach.Encounter E
		INNER JOIN tbreach.EncounterResults ER ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
		INNER JOIN definition D ON D.definition_type = 'ENCOUNTER_TYPE' AND D.definition_value = E.EncounterType
		WHERE DATE(E.DateEncounterStart) >= '2011-01-02' AND DATE(E.DateEncounterStart) <= curdate()
--		WHERE E.EncounterType IN ('BASELINE', 'CDF', 'DFR', 'DRUG_ADM', 'FOLLOW_UP', 'PAED_DIAG')
--		AND ER.Element IN ('ENTERED_DATE', 'CONCLUSION', 'DIAGNOSIS', 'MONTH');
	TRUNCATE dim_genexpert_results; 
	INSERT INTO dim_genexpert_results (patient_id, sputum_code, laboratory_id, irs_no, date_submitted, date_tested, is_positive, drug_resistance, remarks)
		SELECT PatientID, SputumTestID, LaboratoryID, IRS, DateSubmitted, DateTested, IsPositive, DrugResistance, Remarks 
		FROM tbreach.GeneXpertResults;
	TRUNCATE dim_geography;
	INSERT INTO dim_geography (person_id, street, sector, town, uc, landmark, latitude, longitude, city, state, country)
		SELECT C.PID, C.AddressStreet, C.AddressSector, C.AddressTown, C.AddressUC, C.AddressLandmark, C.AddressLocationLat, C.AddressLocationLon, Cty.CityName, NULL, Ctry.CountryName 
		FROM tbreach.Contact C
		LEFT OUTER JOIN tbreach.SetupCountry Ctry ON Ctry.CountryID = C.CountryID
		LEFT OUTER JOIN tbreach.SetupCity Cty ON Cty.CountryID = C.CountryID AND Cty.CityID = C.CityID;
	TRUNCATE dim_gp;
	INSERT INTO dim_gp (gpid, full_name, clinic_geography_key, gender, phone, mobile)	
		SELECT G.GPID AS gpid, CONCAT(P.FirstName, ' ', P.LastName) AS full_name, NULL, P.Gender, C.Phone,  C.Mobile
		FROM tbreach.GP AS G
		LEFT OUTER JOIN tbreach.Person P ON P.PID = G.GPID
		LEFT OUTER JOIN tbreach.Contact C ON C.PID = G.GPID
		WHERE G.GPID NOT IN (SELECT distinct gpid from dim_gp);
	TRUNCATE dim_incentive;
	INSERT INTO dim_incentive (person_id, person_role, transaction_no, incentive_type, amount, status, date_transferred, remarks)
		SELECT I.PID, SUBSTR(I.PID, 1, 1) AS PersonRole, I.TransactionNo, D.definition_key AS IncentiveType, S.Amount, D2.definition_key, I.DateTransferred, I.Remarks 
		FROM tbreach.Incentive I
		INNER JOIN tbreach.Person P ON P.PID = I.PID
		INNER JOIN tbreach.SetupIncentive S ON S.IncentiveID = I.IncentiveID
		INNER JOIN Definition D ON D.definition_type = 'INCENTIVE_TYPE' AND D.definition_value = I.IncentiveID
		INNER JOIN Definition D2 ON D2.definition_type = 'INCENTIVE_STATUS' AND D2.definition_value = I.Status;
	TRUNCATE dim_monitor;
	INSERT INTO dim_monitor (monitor_id, full_name, phone, mobile)	
		SELECT M.MonitorID AS gpid, CONCAT(P.FirstName, ' ', P.LastName) AS full_name, C.Phone,  C.Mobile
		FROM tbreach.Monitor AS M
		LEFT OUTER JOIN tbreach.Person P ON P.PID = M.MonitorID
		LEFT OUTER JOIN tbreach.Contact C ON C.PID = M.MonitorID
		WHERE M.MonitorID NOT IN (SELECT distinct monitor_id from dim_monitor);
	TRUNCATE dim_patient;
	INSERT INTO dim_patient (patient_id, chwid, gpid, mr_no, full_name, dob, age, gender, marital_status, religion, caste, alive, picture, geography_key, phone, mobile, email, weight, height, date_registered, treatment_centre, disease_category, disease_site, regimen, dose, streptomycin, treatment_phase, patient_type, disease_history, treated_previously, completed_previous_treatment)
		SELECT P.PatientID, P.CHWID, P.GPID, P.MRNo, CONCAT(Pr.FirstName, ' ', Pr.LastName) AS full_name, Pr.DOB, YEAR(CURDATE()) - YEAR(DOB) AS Age, Pr.Gender, D_MaritalStatus.definition_key AS MaritalStatus, D_Religion.definition_key AS Religion, D_Caste.definition_key AS Caste, Pr.Alive, Pr.Picture, D.geography_key, C.Phone, C.Mobile, C.Email, P.Weight, P.Height, P.DateRegistered, P.TreatmentCenter, 
		D_DiseaseCategory.definition_key AS DiseaseCategory, D_DiseaseSite.definition_key AS DiseaseSite, D_Regimen.definition_key AS Regimen, D_DoseCombination.definition_key AS DoseCombination, D_Streptomycin.definition_key AS Streptomycin, D_TreatmentPhase.definition_key AS TreatmentPhase, D_PatientType.definition_key AS PatientType, P.DiseaseHistory, P.TreatedPreviously, P.CompletedPreviousTreatment
		FROM tbreach.Patient P
		INNER JOIN tbreach.Person Pr ON Pr.PID = P.PatientID
		INNER JOIN tbreach.Contact C ON C.PID = P.PatientID
		LEFT OUTER JOIN dim_geography D ON D.person_id = P.PatientID
		LEFT OUTER JOIN definition D_MaritalStatus ON D_MaritalStatus.definition_type = 'MARITAL_STATUS' AND D_MaritalStatus.definition_value = Pr.MaritalStatus
		LEFT OUTER JOIN definition D_Religion ON D_Religion.definition_type = 'RELIGION' AND D_Religion.definition_value = Pr.Religion
		LEFT OUTER JOIN definition D_Caste ON D_Caste.definition_type = 'CASTE' AND D_Caste.definition_value = Pr.Caste
		LEFT OUTER JOIN definition D_DiseaseCategory ON D_DiseaseCategory.definition_type = 'DISEASE_CATEGORY' AND D_DiseaseCategory.definition_value = P.DiseaseCategory
		LEFT OUTER JOIN definition D_DiseaseSite ON D_DiseaseSite.definition_type = 'DISEASE_SITE' AND D_DiseaseSite.definition_value = P.DiseaseSite
		LEFT OUTER JOIN definition D_Regimen ON D_Regimen.definition_type = 'REGIMEN' AND D_Regimen.definition_value = P.Regimen
		LEFT OUTER JOIN definition D_DoseCombination ON D_DoseCombination.definition_type = 'DOSE_COMBINATION' AND D_DoseCombination.definition_value = P.DoseCombination
		LEFT OUTER JOIN definition D_Streptomycin ON D_Streptomycin.definition_type = 'STREPTOMYCIN' AND D_Streptomycin.definition_value = P.OtherDoseDescription
		LEFT OUTER JOIN definition D_TreatmentPhase ON D_TreatmentPhase.definition_type = 'TREATMENT_PHASE' AND D_TreatmentPhase.definition_value = P.TreatmentPhase
		LEFT OUTER JOIN definition D_PatientType ON D_PatientType.definition_type = 'PATIENT_TYPE' AND D_PatientType.definition_value = P.PatientType;
	TRUNCATE dim_smear_results;
	INSERT INTO dim_smear_results (patient_id, sputum_code, laboratory_id, irs_no, month, date_submitted, date_tested, tested_by, smear_result, remarks)
		SELECT S.PatientID, S.SputumTestID, S.LaboratoryID, S.IRS, S.Month, S.DateSubmitted, S.DateTested, S.TestedBy, D.definition_key, S.Remarks
		FROM tbreach.SputumResults S
		INNER JOIN Definition D ON D.definition_type = 'SMEAR_RESULT' AND D.definition_value = S.SmearResult;
	TRUNCATE dim_time;
	INSERT INTO dim_time (date, year, quarter, month, week, day)
		SELECT DISTINCT DATE(DateEncounterStart) AS DATE, year(DateEncounterStart) AS year, round(month(DateEncounterStart) / 4) + 1 AS quarter, month(DateEncounterStart) AS month, week(DateEncounterStart) AS week, day(DateEncounterStart) AS day 
		FROM tbreach.encounter
		WHERE DATE(DateEncounterStart) >= '2011-01-02' AND DATE(DateEncounterStart) <= curdate()
		ORDER BY DATE;
	TRUNCATE dim_xray_results;
	INSERT INTO dim_xray_results (patient_id, irs_no, xray_date, date_reported, xray_results, remarks)
		SELECT X.PatientID, X.IRS, X.XRayDate, X.DateReported, D.definition_key, X.Remarks
		FROM tbreach.XRayResults X
		LEFT OUTER JOIN definition D ON D.definition_type = 'XRAY_RESULT' AND D.definition_value =  X.XrayResults
	-- Fill fact tables
	TRUNCATE fact_chw_incentives;
	INSERT INTO fact_chw_incentives (time_key, chw_key, incentive_type, total_amount, amount_pending)
		SELECT t.time_key, c.chw_key, i.incentive_type, SUM(i.amount) AS total_amount, 
		(SELECT SUM(amount) FROM dim_incentive WHERE person_id = i.person_id AND person_role = i.person_role AND incentive_type = i.incentive_type AND DATE(date_transferred) = t.date AND status = 'P') AS amount_pending 
		FROM dim_chw c
		INNER JOIN dim_incentive i ON i.person_id = c.chwid AND i.person_role = 'C'
		INNER JOIN dim_time t ON t.date = date(i.date_transferred)
		GROUP BY t.time_key, c.chw_key, i.incentive_type;
	TRUNCATE fact_sputum_collection
	INSERT INTO fact_sputum_collection 
-- TO BE FILLED
	TRUNCATE fact_sputum_rejection
	INSERT INTO fact_sputum_rejection (time_key, person_key, person_type, total_collected, total_positive, positive_ratio)
	
END
