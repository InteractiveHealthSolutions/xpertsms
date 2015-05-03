/**
SELECT * FROM Encounter AS E
INNER JOIN EncounterResults AS ER ON E.EncounterID = ER.EncounterID AND E.PID1 = ER.PID1 AND E.PID2 = ER.PID2
WHERE ER.Element='DIAGNOSIS'
*/

SELECT * FROM EncounterResults AS ER 
WHERE ER.Element = 'DIAGNOSIS' AND ER.PID1 = 'P1911031901'

-- Select Salary Summary
SELECT PID AS StaffID, DFR, Sputum1, Sputum2, SmearPositive, GeneXpertPositive, AdultPTB, AdultEPTB, PaediatricDiagnosis, Baseline, Followup, TreatmentComplete, Cured,  
(DFR * 75) AS DFRRate, (Sputum1 * 75) AS Sputum1Total, (Sputum2 * 75) AS Sputum2Total, (SmearPositive * 1000) AS SmearTotal, (GeneXpertPositive * 1000) AS GeneXpertTotal, 
(AdultPTB * 500) AS PTBTotal, (AdultEPTB * 500) AS EPTBTotal, (PaediatricDiagnosis * 500) AS PaedsTotal, (Baseline * 150) AS BaselineTotal, (Followup * 50) AS FollowupTotal, (TreatmentComplete * 400) AS TreatmentCompleteTotal, (Cured * 600) AS CuredTotal FROM tmp_SummaryForIncentive;

-- DFRs
SELECT DISTINCT PID1 AS CHWID, COUNT(*) AS DFR FROM Encounter
WHERE EncounterType = 'DFR'
GROUP BY PID1

-- Sputum Sample 1
SELECT DISTINCT E.PID2, COUNT(*) AS Sputum1 FROM Encounter AS E
INNER JOIN EncounterResults AS ER ON E.EncounterID = ER.EncounterID AND E.PID1 = ER.PID1 AND E.PID2 = ER.PID2
WHERE E.EncounterType = 'SPUTUM_COL' AND ER.Element = 'SUSPECT_SAMPLE' AND ER.Value = '1'
GROUP BY E.PID2

-- Sputum Sample 2
SELECT DISTINCT E.PID2, COUNT(*) AS Sputum1 FROM Encounter AS E
INNER JOIN EncounterResults AS ER ON E.EncounterID = ER.EncounterID AND E.PID1 = ER.PID1 AND E.PID2 = ER.PID2
WHERE E.EncounterType = 'SPUTUM_COL' AND ER.Element = 'SUSPECT_SAMPLE' AND ER.Value = '2'
GROUP BY E.PID2

-- Smear Positives
SELECT DISTINCT P.CHWID, COUNT(*) AS SmearPositive FROM Patient AS P
INNER JOIN SputumResults AS S ON S.PatientID = P.PatientID  
WHERE S.SmearResult IN ('1+', '2+', '3+', '1-9AFB') AND P.CHWID IS NOT NULL
GROUP BY P.CHWID

-- Gene Xpert Positives
SELECT DISTINCT P.CHWID, COUNT(*) AS GeneXpert FROM Patient AS P
INNER JOIN GeneXpertResults AS G ON G.PatientID = P.PatientID  
WHERE G.IsPositive = 1 AND P.CHWID IS NOT NULL AND P.PatientID NOT IN (SELECT DISTINCT PatientID FROM SputumResults WHERE SmearResult NOT IN ('1+', '2+', '3+', '1-9AFB'))
GROUP BY P.CHWID

-- Paediatric clinical diagnosis
SELECT DISTINCT P.CHWID, COUNT(*) AS PaediatricDiagnosis FROM Patient AS P
INNER JOIN Encounter AS E ON E.PID1 = P.PatientID
INNER JOIN EncounterResults AS ER ON E.EncounterID = ER.EncounterID AND E.PID1 = ER.PID1 AND E.PID2 = ER.PID2
WHERE E.EncounterType = 'PAED_DIAG' AND P.CHWID IS NOT NULL AND ER.Element = 'CONCLUSION' AND ER.Value IN ('EXTRA PULMONARY PEDIATRIC TB', 'SMEAR POSITVE PULMONARY PEDIATRIC TB', 'SMEAR NEGATIVE PULMONARY PEDIATRIC TB') 
	AND P.PatientID NOT IN (SELECT PatientID FROM SputumResults WHERE SmearResult NOT IN ('1+', '2+', '3+', '1-9AFB'))
	AND P.PatientID NOT IN (SELECT PatientID FROM GeneXpertResults WHERE IsPositive = 1)
GROUP BY P.CHWID

-- Baseline visits
SELECT DISTINCT P.CHWID, COUNT(*) AS Baseline FROM Patient AS P
INNER JOIN Encounter AS E ON E.PID1 = P.PatientID
INNER JOIN EncounterResults AS ER ON E.EncounterID = ER.EncounterID AND E.PID1 = ER.PID1 AND E.PID2 = ER.PID2
WHERE E.EncounterType = 'BASELINE' AND ER.Element = 'ENTERED_DATE' AND P.CHWID IS NOT NULL 
GROUP BY P.CHWID

-- Follow-up visits
SELECT DISTINCT P.CHWID, COUNT(*) AS Baseline FROM Patient AS P
INNER JOIN Encounter AS E ON E.PID1 = P.PatientID
INNER JOIN EncounterResults AS ER ON E.EncounterID = ER.EncounterID AND E.PID1 = ER.PID1 AND E.PID2 = ER.PID2
WHERE E.EncounterType = 'FOLLOW_UP' AND ER.Element = 'ENTERED_DATE' AND P.CHWID IS NOT NULL AND MONTH(E.DateEncounterEntered) = 4
GROUP BY P.CHWID

-- Follow-up Progress Report (CAT I)
SELECT P.PatientID, E.PID2 as GPID, P.MRNo, S.SmearResult, G.IsPositive, 
(SELECT ER.Value FROM Encounter E 
	INNER JOIN EncounterResults ER ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2 
	WHERE E.EncounterType = 'CDF' AND ER.Element = 'DIAGNOSIS' AND ER.Value IN ('SMEAR -VE PULMONARY TB', 'EP-TB') AND E.PID1 = P.PatientID) AS AdultDiagnosis, 
P.TreatmentPhase, P.DiseaseCategory, P.DiseaseSite, MAX(E.DateEncounterEntered) AS BaselineDate, CAST(DATEDIFF(CURDATE(), MAX(E.DateEncounterEntered)) AS DECIMAL)/30.5 AS TotalPeriod,
(SELECT MAX(E.DateEncounterEntered) FROM Encounter E INNER JOIN EncounterResults ER ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
	WHERE E.PID1 = P.PatientID AND E.EncounterType = 'FOLLOW_UP' AND ER.Element = 'MONTH' AND ER.Value = '2') AS Month2Date,
CASE SR2.SmearResult WHEN 'NEGATIVE' THEN MIN(SR2.DateSubmitted) ELSE MAX(SR2.DateSubmitted) END AS Smear2Date, SR2.SmearResult AS Smear2Result,
(SELECT MAX(E.DateEncounterEntered) FROM Encounter E INNER JOIN EncounterResults ER ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
	WHERE E.PID1 = P.PatientID AND E.EncounterType = 'FOLLOW_UP' AND ER.Element = 'MONTH' AND ER.Value = '3') AS Month3Date,
CASE SR3.SmearResult WHEN 'NEGATIVE' THEN MIN(SR3.DateSubmitted) ELSE MAX(SR3.DateSubmitted) END AS Smear3Date, SR3.SmearResult AS Smear3Result,
(SELECT MAX(E.DateEncounterEntered) FROM Encounter E INNER JOIN EncounterResults ER ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
	WHERE E.PID1 = P.PatientID AND E.EncounterType = 'FOLLOW_UP' AND ER.Element = 'MONTH' AND ER.Value = '5') AS Month5Date,
CASE SR5.SmearResult WHEN 'NEGATIVE' THEN MIN(SR5.DateSubmitted) ELSE MAX(SR5.DateSubmitted) END AS Smear5Date, SR5.SmearResult AS Smear5Result,
(SELECT MAX(E.DateEncounterEntered) FROM Encounter E INNER JOIN EncounterResults ER ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
	WHERE E.PID1 = P.PatientID AND E.EncounterType = 'FOLLOW_UP' AND ER.Element = 'MONTH' AND ER.Value = '7') AS Month7Date,
CASE SR7.SmearResult WHEN 'NEGATIVE' THEN MIN(SR7.DateSubmitted) ELSE MAX(SR7.DateSubmitted) END AS Smear7Date, SR7.SmearResult AS Smear7Result
FROM Patient AS P
INNER JOIN Encounter AS E ON P.PatientID = E.PID1
LEFT OUTER JOIN SputumResults S ON S.PatientID = P.PatientID AND S.SmearResult IN ('1+', '2+', '3+', '1-9AFB')
LEFT OUTER JOIN GeneXpertResults G ON G.PatientID = P.PatientID AND G.IsPositive = 1
LEFT OUTER JOIN SputumResults SR2 ON SR2.PatientID = P.PatientID AND SR2.Month = 2 AND SR2.SmearResult IS NOT NULL
LEFT OUTER JOIN SputumResults SR3 ON SR3.PatientID = P.PatientID AND SR3.Month = 3 AND SR3.SmearResult IS NOT NULL
LEFT OUTER JOIN SputumResults SR5 ON SR5.PatientID = P.PatientID AND SR5.Month = 5 AND SR5.SmearResult IS NOT NULL
LEFT OUTER JOIN SputumResults SR7 ON SR7.PatientID = P.PatientID AND SR5.Month = 7 AND SR7.SmearResult IS NOT NULL
WHERE P.PatientStatus <> 'CLOSED' AND P.DiseaseCategory = 'CAT I' AND E.EncounterType = 'BASELINE' AND P.PatientID NOT IN 
(SELECT E.PID1 FROM EncounterResults AS ER INNER JOIN Encounter AS E ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
WHERE E.EncounterType = 'PAED_DIAG' AND ER.Element = 'CONCLUSION' AND ER.Value IN 
('EXTRA PULMONARY PEDIATRIC TB', 'SMEAR POSITVE PULMONARY PEDIATRIC TB', 'SMEAR NEGATIVE PULMONARY PEDIATRIC TB'))
GROUP BY P.PatientID, E.PID2, P.MRNo, P.TreatmentPhase, P.DiseaseSite;
-- Alternate Follow-up Progress Report (CAT I)
SELECT P.PatientID, E.PID2 as GPID, P.MRNo, S.SmearResult, G.IsPositive, 
(SELECT Value FROM tmp_EncounterResults WHERE EncounterType = 'CDF' AND Element = 'DIAGNOSIS' AND Value IN ('SMEAR -VE PULMONARY TB', 'EP-TB') AND PID1 = P.PatientID) AS AdultDiagnosis, 
P.TreatmentPhase, P.DiseaseCategory, P.DiseaseSite, MAX(E.DateEncounterEntered) AS BaselineDate, CAST(DATEDIFF(CURDATE(), MAX(E.DateEncounterEntered)) AS DECIMAL)/30.5 AS TotalPeriod,
(SELECT MAX(EnteredDate) FROM tmp_EncounterResults WHERE PID1 = P.PatientID AND EncounterType = 'FOLLOW_UP' AND Element = 'MONTH' AND Value = '2') AS Month2Date,
CASE SR2.SmearResult WHEN 'NEGATIVE' THEN MIN(SR2.DateSubmitted) ELSE MAX(SR2.DateSubmitted) END AS Smear2Date, SR2.SmearResult AS Smear2Result,
(SELECT MAX(EnteredDate) FROM tmp_EncounterResults WHERE PID1 = P.PatientID AND EncounterType = 'FOLLOW_UP' AND Element = 'MONTH' AND Value = '3') AS Month3Date,
CASE SR3.SmearResult WHEN 'NEGATIVE' THEN MIN(SR3.DateSubmitted) ELSE MAX(SR3.DateSubmitted) END AS Smear3Date, SR3.SmearResult AS Smear3Result,
(SELECT MAX(EnteredDate) FROM tmp_EncounterResults WHERE PID1 = P.PatientID AND EncounterType = 'FOLLOW_UP' AND Element = 'MONTH' AND Value = '5') AS Month5Date,
CASE SR5.SmearResult WHEN 'NEGATIVE' THEN MIN(SR5.DateSubmitted) ELSE MAX(SR5.DateSubmitted) END AS Smear5Date, SR5.SmearResult AS Smear5Result,
(SELECT MAX(EnteredDate) FROM tmp_EncounterResults WHERE PID1 = P.PatientID AND EncounterType = 'FOLLOW_UP' AND Element = 'MONTH' AND Value = '7') AS Month7Date,
CASE SR7.SmearResult WHEN 'NEGATIVE' THEN MIN(SR7.DateSubmitted) ELSE MAX(SR7.DateSubmitted) END AS Smear7Date, SR7.SmearResult AS Smear7Result
FROM Patient AS P
INNER JOIN Encounter AS E ON P.PatientID = E.PID1
LEFT OUTER JOIN SputumResults S ON S.PatientID = P.PatientID AND S.SmearResult <> 'NEGATIVE'
LEFT OUTER JOIN GeneXpertResults G ON G.PatientID = P.PatientID AND G.IsPositive = 1
LEFT OUTER JOIN SputumResults SR2 ON SR2.PatientID = P.PatientID AND SR2.Month = 2 AND SR2.SmearResult IS NOT NULL
LEFT OUTER JOIN SputumResults SR3 ON SR3.PatientID = P.PatientID AND SR3.Month = 3 AND SR3.SmearResult IS NOT NULL
LEFT OUTER JOIN SputumResults SR5 ON SR5.PatientID = P.PatientID AND SR5.Month = 5 AND SR5.SmearResult IS NOT NULL
LEFT OUTER JOIN SputumResults SR7 ON SR7.PatientID = P.PatientID AND SR7.Month = 7 AND SR7.SmearResult IS NOT NULL
WHERE P.PatientStatus <> 'CLOSED' AND P.DiseaseCategory = 'CAT I' AND E.EncounterType = 'BASELINE' AND P.PatientID NOT IN 
(SELECT E.PID1 FROM EncounterResults AS ER INNER JOIN Encounter AS E ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
WHERE E.EncounterType = 'PAED_DIAG' AND ER.Element = 'CONCLUSION' AND ER.Value IN 
('EXTRA PULMONARY PEDIATRIC TB', 'SMEAR POSITVE PULMONARY PEDIATRIC TB', 'SMEAR NEGATIVE PULMONARY PEDIATRIC TB'))
GROUP BY P.PatientID, E.PID2, P.MRNo, P.TreatmentPhase, P.DiseaseSite;

-- Follow-up Progress Report (CAT II)
SELECT P.PatientID, E.PID2 as GPID, P.MRNo, S.SmearResult, G.IsPositive, 
(SELECT ER.Value FROM Encounter E 
	INNER JOIN EncounterResults ER ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2 
	WHERE E.EncounterType = 'CDF' AND ER.Element = 'DIAGNOSIS' AND ER.Value IN ('SMEAR -VE PULMONARY TB', 'EP-TB') AND E.PID1 = P.PatientID) AS AdultDiagnosis, 
P.TreatmentPhase, P.DiseaseCategory, P.DiseaseSite, MAX(E.DateEncounterEntered) AS BaselineDate, CAST(DATEDIFF(CURDATE(), MAX(E.DateEncounterEntered)) AS DECIMAL)/30.5 AS TotalPeriod,
(SELECT MAX(E.DateEncounterEntered) FROM Encounter E INNER JOIN EncounterResults ER ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
	WHERE E.PID1 = P.PatientID AND E.EncounterType = 'FOLLOW_UP' AND ER.Element = 'MONTH' AND ER.Value = '3') AS Month3Date,
CASE SR3.SmearResult WHEN 'NEGATIVE' THEN MIN(SR3.DateSubmitted) ELSE MAX(SR3.DateSubmitted) END AS Smear3Date, SR3.SmearResult AS Smear3Result,
(SELECT MAX(E.DateEncounterEntered) FROM Encounter E INNER JOIN EncounterResults ER ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
	WHERE E.PID1 = P.PatientID AND E.EncounterType = 'FOLLOW_UP' AND ER.Element = 'MONTH' AND ER.Value = '5') AS Month5Date,
CASE SR5.SmearResult WHEN 'NEGATIVE' THEN MIN(SR5.DateSubmitted) ELSE MAX(SR5.DateSubmitted) END AS Smear5Date, SR5.SmearResult AS Smear5Result,
(SELECT MAX(E.DateEncounterEntered) FROM Encounter E INNER JOIN EncounterResults ER ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
	WHERE E.PID1 = P.PatientID AND E.EncounterType = 'FOLLOW_UP' AND ER.Element = 'MONTH' AND ER.Value = '7') AS Month7Date,
CASE SR7.SmearResult WHEN 'NEGATIVE' THEN MIN(SR7.DateSubmitted) ELSE MAX(SR7.DateSubmitted) END AS Smear7Date, SR7.SmearResult AS Smear7Result
FROM Patient AS P
INNER JOIN Encounter AS E ON P.PatientID = E.PID1
LEFT OUTER JOIN SputumResults S ON S.PatientID = P.PatientID AND S.SmearResult IN ('1+', '2+', '3+', '1-9AFB')
LEFT OUTER JOIN GeneXpertResults G ON G.PatientID = P.PatientID AND G.IsPositive = 1
LEFT OUTER JOIN SputumResults SR3 ON SR3.PatientID = P.PatientID AND SR3.Month = 3 AND SR3.SmearResult IS NOT NULL
LEFT OUTER JOIN SputumResults SR5 ON SR5.PatientID = P.PatientID AND SR5.Month = 5 AND SR5.SmearResult IS NOT NULL
LEFT OUTER JOIN SputumResults SR7 ON SR7.PatientID = P.PatientID AND SR7.Month = 7 AND SR7.SmearResult IS NOT NULL
WHERE P.PatientStatus <> 'CLOSED' AND P.DiseaseCategory = 'CAT II' AND E.EncounterType = 'BASELINE' AND P.PatientID NOT IN 
(SELECT E.PID1 FROM EncounterResults AS ER INNER JOIN Encounter AS E ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
WHERE E.EncounterType = 'PAED_DIAG' AND ER.Element = 'CONCLUSION' AND ER.Value IN 
('EXTRA PULMONARY PEDIATRIC TB', 'SMEAR POSITVE PULMONARY PEDIATRIC TB', 'SMEAR NEGATIVE PULMONARY PEDIATRIC TB'))
GROUP BY P.PatientID, E.PID2, P.MRNo, P.TreatmentPhase, P.DiseaseSite;
-- Alternate Follow-up Progress Report (CAT II)
SELECT P.PatientID, E.PID2 as GPID, P.MRNo, S.SmearResult, G.IsPositive, 
(SELECT Value FROM tmp_EncounterResults WHERE EncounterType = 'CDF' AND Element = 'DIAGNOSIS' AND Value IN ('SMEAR -VE PULMONARY TB', 'EP-TB') AND PID1 = P.PatientID) AS AdultDiagnosis, 
P.TreatmentPhase, P.DiseaseCategory, P.DiseaseSite, MAX(E.DateEncounterEntered) AS BaselineDate, CAST(DATEDIFF(CURDATE(), MAX(E.DateEncounterEntered)) AS DECIMAL)/30.5 AS TotalPeriod,
(SELECT MAX(EnteredDate) FROM tmp_EncounterResults WHERE PID1 = P.PatientID AND EncounterType = 'FOLLOW_UP' AND Element = 'MONTH' AND Value = '3') AS Month3Date,
CASE SR3.SmearResult WHEN 'NEGATIVE' THEN MIN(SR3.DateSubmitted) ELSE MAX(SR3.DateSubmitted) END AS Smear3Date, SR3.SmearResult AS Smear3Result,
(SELECT MAX(EnteredDate) FROM tmp_EncounterResults WHERE PID1 = P.PatientID AND EncounterType = 'FOLLOW_UP' AND Element = 'MONTH' AND Value = '5') AS Month5Date,
CASE SR5.SmearResult WHEN 'NEGATIVE' THEN MIN(SR5.DateSubmitted) ELSE MAX(SR5.DateSubmitted) END AS Smear5Date, SR5.SmearResult AS Smear5Result,
(SELECT MAX(EnteredDate) FROM tmp_EncounterResults WHERE PID1 = P.PatientID AND EncounterType = 'FOLLOW_UP' AND Element = 'MONTH' AND Value = '7') AS Month7Date,
CASE SR7.SmearResult WHEN 'NEGATIVE' THEN MIN(SR7.DateSubmitted) ELSE MAX(SR7.DateSubmitted) END AS Smear7Date, SR7.SmearResult AS Smear7Result
FROM Patient AS P
INNER JOIN Encounter AS E ON P.PatientID = E.PID1
LEFT OUTER JOIN SputumResults S ON S.PatientID = P.PatientID AND S.SmearResult <> 'NEGATIVE'
LEFT OUTER JOIN GeneXpertResults G ON G.PatientID = P.PatientID AND G.IsPositive = 1
LEFT OUTER JOIN SputumResults SR3 ON SR3.PatientID = P.PatientID AND SR3.Month = 3 AND SR3.SmearResult IS NOT NULL
LEFT OUTER JOIN SputumResults SR5 ON SR5.PatientID = P.PatientID AND SR5.Month = 5 AND SR5.SmearResult IS NOT NULL
LEFT OUTER JOIN SputumResults SR7 ON SR7.PatientID = P.PatientID AND SR7.Month = 7 AND SR7.SmearResult IS NOT NULL
WHERE P.PatientStatus <> 'CLOSED' AND P.DiseaseCategory = 'CAT II' AND E.EncounterType = 'BASELINE' AND P.PatientID NOT IN 
(SELECT E.PID1 FROM EncounterResults AS ER INNER JOIN Encounter AS E ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
WHERE E.EncounterType = 'PAED_DIAG' AND ER.Element = 'CONCLUSION' AND ER.Value IN 
('EXTRA PULMONARY PEDIATRIC TB', 'SMEAR POSITVE PULMONARY PEDIATRIC TB', 'SMEAR NEGATIVE PULMONARY PEDIATRIC TB'))
GROUP BY P.PatientID, E.PID2, P.MRNo, P.TreatmentPhase, P.DiseaseSite;

-- XRay Reporting Log
SELECT DISTINCT L.UserID, DATE(L.DateChanged) AS DateUpdated, SUBSTRING(L.NewValue, 9, 11) AS PatientID, P.MRNo FROM Log_DataChange L
LEFT OUTER JOIN Patient P ON P.PatientID = SUBSTRING(L.NewValue, 9, 11)
WHERE L.Entity = 'XRayResults' AND L.UserID <> ''
ORDER BY L.UserID

-- Follow-up Visits Schedule Report
SELECT T.PatientID, T.MRNo, T.DiseaseCategory, T.DateBaselineVisit, DATE_ADD(DateBaselineVisit, INTERVAL 2 MONTH) AS Schedule1 FROM 
(SELECT DISTINCT P.PatientID, P.MRNo, P.DiseaseCategory, 
	(SELECT CAST(MAX(STR_TO_DATE(Value, "%d/%m/%Y")) AS DATE) AS DateEncountered FROM Encounter E 
		INNER JOIN EncounterResults Er ON Er.EncounterID = E.EncounterID AND Er.PID1 = E.PID1 AND Er.PID2 = E.PID2 AND Er.Element = 'ENTERED_DATE' WHERE E.EncounterType = 'BASELINE' AND E.PID1 = S.PatientID) AS DateBaselineVisit FROM SputumResults S 
		INNER JOIN Patient P ON P.PatientID = S.PatientID 
		WHERE S.SmearResult <> 'NEGATIVE' AND P.DiseaseCategory IS NOT NULL AND S.SmearResult IS NOT NULL AND P.PatientStatus <> 'CLOSED') AS T
UNION
SELECT T.PatientID, T.MRNo, T.DiseaseCategory, T.DateBaselineVisit, DATE_ADD(DateBaselineVisit, INTERVAL 2 MONTH) AS Schedule1 FROM
(SELECT DISTINCT G.PatientID, P.MRNo, P.DiseaseCategory,
	(SELECT CAST(MAX(STR_TO_DATE(Value, "%d/%m/%Y")) AS DATE) AS DateEncountered FROM Encounter E 
		INNER JOIN EncounterResults Er ON Er.EncounterID = E.EncounterID AND Er.PID1 = E.PID1 AND Er.PID2 = E.PID2 AND Er.Element = 'ENTERED_DATE' WHERE E.EncounterType = 'BASELINE' AND E.PID1 = G.PatientID) AS DateBaselineVisit FROM GeneXpertResults G
		INNER JOIN Patient P ON P.PatientID = G.PatientID
		WHERE G.IsPositive = 1 AND P.DiseaseCategory IS NOT NULL AND P.PatientStatus <> 'CLOSED' AND P.PatientID NOT IN (SELECT DISTINCT PatientID FROM SputumResults WHERE SmearResult IS NOT NULL AND SmearResult <> 'NEGATIVE')) AS T
UNION
SELECT T.PatientID, T.MRNo, T.DiseaseCategory, T.DateBaselineVisit, DATE_ADD(DateBaselineVisit, INTERVAL 2 MONTH) AS Schedule1 FROM
(SELECT DISTINCT P.PatientID, P.MRNo, P.DiseaseCategory,
	(SELECT CAST(MAX(STR_TO_DATE(Value, "%d/%m/%Y")) AS DATE) AS DateEncountered FROM Encounter E2 INNER JOIN EncounterResults Er2 ON Er2.EncounterID = E2.EncounterID AND Er2.PID1 = E2.PID1 AND Er2.PID2 = E2.PID2 AND Er2.Element = 'ENTERED_DATE' WHERE E2.EncounterType = 'BASELINE' AND E2.PID1 = E.PID1) AS DateBaselineVisit FROM EncounterResults AS ER
		INNER JOIN Encounter AS E ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
		INNER JOIN Patient P ON P.PatientID = E.PID1
		WHERE P.DiseaseCategory IS NOT NULL AND E.EncounterType = 'CDF' AND ER.Element = 'DIAGNOSIS' AND ER.Value IN ('SMEAR -VE PULMONARY TB', 'EP-TB') AND P.PatientStatus <> 'CLOSED' AND P.PatientID NOT IN (SELECT DISTINCT PatientID FROM SputumResults WHERE SmearResult IS NOT NULL AND SmearResult <> 'NEGATIVE') AND P.PatientID NOT IN (SELECT PatientID FROM GeneXpertResults WHERE IsPositive = 1)) AS T
UNION
SELECT T.PatientID, T.MRNo, T.DiseaseCategory, T.DateBaselineVisit, DATE_ADD(DateBaselineVisit, INTERVAL 2 MONTH) AS Schedule1 FROM
(SELECT P.PatientID, P.MRNo, P.DiseaseCategory,
	(SELECT CAST(MAX(STR_TO_DATE(Value, "%d/%m/%Y")) AS DATE) AS DateEncountered FROM Encounter E2 INNER JOIN EncounterResults Er2 ON Er2.EncounterID = E2.EncounterID AND Er2.PID1 = E2.PID1 AND Er2.PID2 = E2.PID2 AND Er2.Element = 'ENTERED_DATE' WHERE E2.EncounterType = 'BASELINE' AND E2.PID1 = E.PID1) AS DateBaselineVisit FROM EncounterResults AS ER
		INNER JOIN Encounter AS E ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
		INNER JOIN Patient P ON P.PatientID = E.PID1
		WHERE P.DiseaseCategory IS NOT NULL AND E.EncounterType = 'PAED_DIAG' AND ER.Element = 'CONCLUSION' AND ER.Value IN ('EXTRA PULMONARY PEDIATRIC TB', 'SMEAR POSITVE PULMONARY PEDIATRIC TB', 'SMEAR NEGATIVE PULMONARY PEDIATRIC TB') AND P.PatientStatus <> 'CLOSED' AND P.PatientID NOT IN (SELECT DISTINCT PatientID FROM SputumResults WHERE SmearResult IS NOT NULL AND SmearResult <> 'NEGATIVE') AND P.PatientID NOT IN (SELECT PatientID FROM GeneXpertResults WHERE IsPositive = 1)) AS T
-- Alternate Follow-up Visits Schedule Report
SELECT T.PatientID, T.MRNo, T.DiseaseCategory, T.DateBaselineVisit, DATE_ADD(DateBaselineVisit, INTERVAL 2 MONTH) AS Schedule1 FROM 
(SELECT DISTINCT P.PatientID, P.MRNo, P.DiseaseCategory, (SELECT MaxEnteredDate FROM tmp_Encounter E WHERE E.EncounterType = 'BASELINE' AND E.PID = P.PatientID) AS DateBaselineVisit 
	FROM Patient P 
	INNER JOIN tmp_Encounter S ON P.PatientID = S.PID 
	WHERE S.EncounterType = 'SMEAR_POSITIVE' AND P.DiseaseCategory IS NOT NULL AND P.PatientStatus <> 'CLOSED') AS T
UNION
SELECT T.PatientID, T.MRNo, T.DiseaseCategory, T.DateBaselineVisit, DATE_ADD(DateBaselineVisit, INTERVAL 2 MONTH) AS Schedule1 FROM
(SELECT DISTINCT G.PatientID, P.MRNo, P.DiseaseCategory, (SELECT MaxEnteredDate FROM tmp_Encounter E WHERE E.EncounterType = 'BASELINE' AND E.PID = G.PatientID) AS DateBaselineVisit 
	FROM GeneXpertResults G
	INNER JOIN Patient P ON P.PatientID = G.PatientID
	WHERE G.IsPositive = 1 AND P.DiseaseCategory IS NOT NULL AND P.PatientStatus <> 'CLOSED' AND P.PatientID NOT IN (SELECT DISTINCT PatientID FROM SputumResults WHERE SmearResult IS NOT NULL AND SmearResult <> 'NEGATIVE')) AS T
UNION
SELECT T.PatientID, T.MRNo, T.DiseaseCategory, T.DateBaselineVisit, DATE_ADD(DateBaselineVisit, INTERVAL 2 MONTH) AS Schedule1 FROM
(SELECT DISTINCT P.PatientID, P.MRNo, P.DiseaseCategory, (SELECT MaxEnteredDate FROM tmp_Encounter E WHERE E.EncounterType = 'BASELINE' AND E.PID = P.PatientID) AS DateBaselineVisit 
	FROM Encounter AS E
	INNER JOIN EncounterResults AS ER ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
	INNER JOIN Patient P ON P.PatientID = E.PID1
	WHERE P.DiseaseCategory IS NOT NULL AND E.EncounterType = 'CDF' AND ER.Element = 'DIAGNOSIS' AND ER.Value IN ('SMEAR -VE PULMONARY TB', 'EP-TB') AND P.PatientStatus <> 'CLOSED' AND P.PatientID NOT IN (SELECT DISTINCT PatientID FROM SputumResults WHERE SmearResult IS NOT NULL AND SmearResult <> 'NEGATIVE') AND P.PatientID NOT IN (SELECT PatientID FROM GeneXpertResults WHERE IsPositive = 1)) AS T
UNION
SELECT T.PatientID, T.MRNo, T.DiseaseCategory, T.DateBaselineVisit, DATE_ADD(DateBaselineVisit, INTERVAL 2 MONTH) AS Schedule1 FROM
(SELECT P.PatientID, P.MRNo, P.DiseaseCategory, (SELECT MaxEnteredDate FROM tmp_Encounter E WHERE E.EncounterType = 'BASELINE' AND E.PID = P.PatientID) AS DateBaselineVisit 
	FROM Encounter AS E
	INNER JOIN EncounterResults AS ER ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
	INNER JOIN Patient P ON P.PatientID = E.PID1
	WHERE P.DiseaseCategory IS NOT NULL AND E.EncounterType = 'PAED_DIAG' AND ER.Element = 'CONCLUSION' AND ER.Value IN ('EXTRA PULMONARY PEDIATRIC TB', 'SMEAR POSITVE PULMONARY PEDIATRIC TB', 'SMEAR NEGATIVE PULMONARY PEDIATRIC TB') AND P.PatientStatus <> 'CLOSED' AND P.PatientID NOT IN (SELECT DISTINCT PatientID FROM SputumResults WHERE SmearResult IS NOT NULL AND SmearResult <> 'NEGATIVE') AND P.PatientID NOT IN (SELECT PatientID FROM GeneXpertResults WHERE IsPositive = 1)) AS T;

-- Confirmed TB Patients Report
SELECT DISTINCT 'SMEAR POSITIVE' AS Source, 
P.PatientID, P.MRNo, P.GPID, P.DiseaseCategory AS Category, P.CHWID, S.SmearResult AS Diagnosis, MIN(S.DateTested) AS DateTested,
(SELECT MAX(E.DateEncounterEntered) FROM Encounter E WHERE E.EncounterType = 'BASELINE' AND E.DateEncounterEntered IS NOT NULL AND E.PID1 = P.PatientID) AS DateBaselineVisit
FROM SputumResults S
INNER JOIN Patient P ON P.PatientID = S.PatientID
LEFT OUTER JOIN GeneXpertResults G ON G.PatientID = P.PatientID
WHERE S.SmearResult IS NOT NULL AND S.SmearResult <> 'NEGATIVE' AND P.PatientStatus <> 'CLOSED'
GROUP BY P.PatientID, P.MRNo, P.GPID, P.MonitorID, P.CHWID
UNION
SELECT DISTINCT 'GENE XPERT' AS Source, 
G.PatientID, P.MRNo, P.GPID, P.DiseaseCategory AS Category, P.CHWID, 'XPERT POSITIVE' AS Diagnosis, G.DateTested AS DateTested,
(SELECT MAX(E.DateEncounterEntered) FROM Encounter E WHERE E.EncounterType = 'BASELINE' AND E.DateEncounterEntered IS NOT NULL AND E.PID1 = P.PatientID) AS DateBaselineVisit
FROM GeneXpertResults G
INNER JOIN Patient P ON P.PatientID = G.PatientID
WHERE G.IsPositive = 1 AND P.PatientStatus <> 'CLOSED' AND P.PatientID NOT IN (SELECT DISTINCT PatientID FROM SputumResults WHERE SmearResult IS NOT NULL AND SmearResult <> 'NEGATIVE')
UNION
SELECT DISTINCT 'ADULT DIAGNOSIS' AS Source, 
P.PatientID, P.MRNo, P.GPID, P.DiseaseCategory AS Category, P.CHWID, ER.Value AS Diagnosis, (SELECT MAX(DateEncounterEntered) FROM Encounter WHERE EncounterType = E.EncounterType AND PID1 = E.PID1 AND PID2 = E.PID2) AS DateTested,
(SELECT MAX(E.DateEncounterEntered) FROM Encounter E WHERE E.EncounterType = 'BASELINE' AND E.DateEncounterEntered IS NOT NULL AND E.PID1 = P.PatientID) AS DateBaselineVisit
FROM EncounterResults AS ER
INNER JOIN Encounter AS E ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
INNER JOIN Patient P ON P.PatientID = E.PID1
WHERE E.EncounterType = 'CDF' AND ER.Element = 'DIAGNOSIS' AND ER.Value IN ('SMEAR -VE PULMONARY TB', 'EP-TB') AND P.PatientStatus <> 'CLOSED' AND P.PatientID NOT IN (SELECT DISTINCT PatientID FROM SputumResults WHERE SmearResult IS NOT NULL AND SmearResult <> 'NEGATIVE') AND P.PatientID NOT IN (SELECT PatientID FROM GeneXpertResults WHERE IsPositive = 1)
UNION
SELECT DISTINCT 'PAEDIATRIC DIAGNOSIS' AS Source, 
P.PatientID, P.MRNo, P.GPID, P.DiseaseCategory AS Category, P.CHWID, ER.Value AS Diagnosis, (SELECT MAX(DateEncounterEntered) FROM Encounter WHERE EncounterType = E.EncounterType AND PID1 = E.PID1 AND PID2 = E.PID2) AS DateTested,
(SELECT MAX(E.DateEncounterEntered) FROM Encounter E WHERE E.EncounterType = 'BASELINE' AND E.DateEncounterEntered IS NOT NULL AND E.PID1 = P.PatientID) AS DateBaselineVisit
FROM EncounterResults AS ER
INNER JOIN Encounter AS E ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
INNER JOIN Patient P ON P.PatientID = E.PID1
WHERE E.EncounterType = 'PAED_DIAG' AND ER.Element = 'CONCLUSION' AND ER.Value IN ('EXTRA PULMONARY PEDIATRIC TB', 'SMEAR POSITVE PULMONARY PEDIATRIC TB', 'SMEAR NEGATIVE PULMONARY PEDIATRIC TB') AND P.PatientStatus <> 'CLOSED' AND P.PatientID NOT IN (SELECT DISTINCT PatientID FROM SputumResults WHERE SmearResult IS NOT NULL AND SmearResult <> 'NEGATIVE') AND P.PatientID NOT IN (SELECT PatientID FROM GeneXpertResults WHERE IsPositive = 1)
-- Alternate Confirmed TB Patients Report
SELECT DISTINCT 'SMEAR POSITIVE' AS Source, 
P.PatientID, P.MRNo, P.GPID, P.DiseaseCategory AS Category, P.CHWID, S.SmearResult AS Diagnosis, MIN(S.DateTested) AS DateTested,
(SELECT MaxEnteredDate FROM tmp_Encounter E WHERE E.EncounterType = 'BASELINE' AND E.PID = P.PatientID) AS DateBaselineVisit
FROM SputumResults S
INNER JOIN Patient P ON P.PatientID = S.PatientID
LEFT OUTER JOIN GeneXpertResults G ON G.PatientID = P.PatientID
WHERE S.SmearResult IS NOT NULL AND S.SmearResult <> 'NEGATIVE' AND P.PatientStatus <> 'CLOSED'
GROUP BY P.PatientID, P.MRNo, P.GPID, P.MonitorID, P.CHWID
UNION
SELECT DISTINCT 'GENE XPERT' AS Source, 
G.PatientID, P.MRNo, P.GPID, P.DiseaseCategory AS Category, P.CHWID, 'XPERT POSITIVE' AS Diagnosis, G.DateTested AS DateTested,
(SELECT MaxEnteredDate FROM tmp_Encounter E WHERE E.EncounterType = 'BASELINE' AND E.PID = P.PatientID) AS DateBaselineVisit
FROM GeneXpertResults G
INNER JOIN Patient P ON P.PatientID = G.PatientID
WHERE G.IsPositive = 1 AND P.PatientStatus <> 'CLOSED' AND P.PatientID NOT IN (SELECT DISTINCT PatientID FROM SputumResults WHERE SmearResult IS NOT NULL AND SmearResult <> 'NEGATIVE')
UNION
SELECT DISTINCT 'ADULT DIAGNOSIS' AS Source, 
P.PatientID, P.MRNo, P.GPID, P.DiseaseCategory AS Category, P.CHWID, ER.Value AS Diagnosis, 
(SELECT MaxEnteredDate FROM tmp_Encounter WHERE EncounterType = E.EncounterType AND PID = E.PID1) AS DateTested,
(SELECT MaxEnteredDate FROM tmp_Encounter E WHERE E.EncounterType = 'BASELINE' AND E.PID = P.PatientID) AS DateBaselineVisit
FROM EncounterResults AS ER
INNER JOIN Encounter AS E ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
INNER JOIN Patient P ON P.PatientID = E.PID1
WHERE E.EncounterType = 'CDF' AND ER.Element = 'DIAGNOSIS' AND ER.Value IN ('SMEAR -VE PULMONARY TB', 'EP-TB') AND P.PatientStatus <> 'CLOSED' AND P.PatientID NOT IN (SELECT DISTINCT PatientID FROM SputumResults WHERE SmearResult IS NOT NULL AND SmearResult <> 'NEGATIVE') AND P.PatientID NOT IN (SELECT PatientID FROM GeneXpertResults WHERE IsPositive = 1)
UNION
SELECT DISTINCT 'PAEDIATRIC DIAGNOSIS' AS Source, 
P.PatientID, P.MRNo, P.GPID, P.DiseaseCategory AS Category, P.CHWID, ER.Value AS Diagnosis,
(SELECT MaxEnteredDate FROM tmp_Encounter WHERE EncounterType = E.EncounterType AND PID = E.PID1) AS DateTested,
(SELECT MaxEnteredDate FROM tmp_Encounter E WHERE E.EncounterType = 'BASELINE' AND E.PID = P.PatientID) AS DateBaselineVisit
FROM EncounterResults AS ER
INNER JOIN Encounter AS E ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
INNER JOIN Patient P ON P.PatientID = E.PID1
WHERE E.EncounterType = 'PAED_DIAG' AND ER.Element = 'CONCLUSION' AND ER.Value IN ('EXTRA PULMONARY PEDIATRIC TB', 'SMEAR POSITVE PULMONARY PEDIATRIC TB', 'SMEAR NEGATIVE PULMONARY PEDIATRIC TB') AND P.PatientStatus <> 'CLOSED' AND P.PatientID NOT IN (SELECT DISTINCT PatientID FROM SputumResults WHERE SmearResult IS NOT NULL AND SmearResult <> 'NEGATIVE') AND P.PatientID NOT IN (SELECT PatientID FROM GeneXpertResults WHERE IsPositive = 1)

-- Adult clinical diagnosis report
SELECT DISTINCT E.PID1, P.MRNo, P.GPID, E.PID2 AS StaffID, MAX(E.DateEncounterEntered) AS DateEntered, ER.Value AS Diagnosis,
	(SELECT MAX(E.DateEncounterEntered) FROM Encounter E WHERE E.EncounterType = 'BASELINE' AND E.DateEncounterEntered IS NOT NULL AND E.PID1 = P.PatientID) AS DateBaselineVisit
FROM EncounterResults AS ER
INNER JOIN Encounter AS E ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
INNER JOIN Patient P ON P.PatientID = E.PID1
WHERE E.EncounterType = 'CDF' AND ER.Element = 'DIAGNOSIS' AND ER.Value IN ('SMEAR -VE PULMONARY TB', 'EP-TB') 
AND P.PatientStatus <> 'CLOSED'
GROUP BY E.PID1, P.MRNo, P.GPID, E.PID2
-- Alternate Adult clinical diagnosis report
SELECT DISTINCT E.PID1, P.MRNo, P.GPID, E.PID2 AS StaffID, MAX(E.DateEncounterEntered) AS DateEntered, ER.Value AS Diagnosis,
	(SELECT MaxEnteredDate FROM tmp_Encounter E WHERE E.EncounterType = 'BASELINE' AND E.DateEncounterEntered IS NOT NULL AND E.PID = P.PatientID) AS DateBaselineVisit
FROM EncounterResults AS ER
INNER JOIN Encounter AS E ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
INNER JOIN Patient P ON P.PatientID = E.PID1
WHERE E.EncounterType = 'CDF' AND ER.Element = 'DIAGNOSIS' AND ER.Value IN ('SMEAR -VE PULMONARY TB', 'EP-TB') 
AND P.PatientStatus <> 'CLOSED'
GROUP BY E.PID1, P.MRNo, P.GPID, E.PID2;

-- Paeds clinical diagnosis report
SELECT DISTINCT E.PID1, P.MRNo, P.GPID, E.PID2 AS StaffID, MAX(E.DateEncounterEntered) AS DateEntered, ER.Value AS Diagnosis,
	(SELECT MAX(E.DateEncounterEntered) FROM Encounter E WHERE E.EncounterType = 'BASELINE' AND E.DateEncounterEntered IS NOT NULL AND E.PID1 = P.PatientID) AS DateBaselineVisit
FROM EncounterResults AS ER
INNER JOIN Encounter AS E ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2
INNER JOIN Patient P ON P.PatientID = E.PID1
WHERE E.EncounterType = 'PAED_DIAG' AND ER.Element = 'CONCLUSION' AND ER.Value IN ('EXTRA PULMONARY PEDIATRIC TB', 'SMEAR POSITVE PULMONARY PEDIATRIC TB', 'SMEAR NEGATIVE PULMONARY PEDIATRIC TB')
AND P.PatientStatus <> 'CLOSED'
GROUP BY E.PID1, P.MRNo, P.GPID, E.PID2

-- X-Ray suggestive report
SELECT P.PatientID, P.GPID, P.MRNo, X.XRayResults, X.Remarks, X.DateReported, MAX(G.DrugResistance) AS DrugResistance, DATE(MIN(S.DateTested)) AS SmearPositiveDate,
	(SELECT IFNULL(Value, '') AS Value FROM EncounterResults WHERE PID1 = P.PatientID AND Element = 'DIAGNOSIS') AS Dignosis,
	(SELECT MAX(E.DateEncounterEntered) FROM Encounter E WHERE E.EncounterType = 'BASELINE' AND E.DateEncounterEntered IS NOT NULL AND E.PID1 = P.PatientID) AS DateBaselineVisit
FROM Patient AS P
INNER JOIN XRayResults AS X ON X.PatientID = P.PatientID
LEFT OUTER JOIN GeneXpertResults AS G ON G.PatientID = P.PatientID
LEFT OUTER JOIN SputumResults AS S ON S.PatientID = P.PatientID AND S.SmearResult <> 'NEGATIVE'
WHERE XRayResults LIKE '%TB%'
GROUP BY P.PatientID, P.GPID, P.MRNo, X.XRayResults, X.DateReported, X.Remarks
-- Alternate X-Ray suggestive report
SELECT P.PatientID, P.GPID, P.MRNo, X.XRayResults, X.Remarks, X.DateReported, G.DrugResistance, S.MinEnteredDate AS SmearPositiveDate, 
(SELECT ER.Value FROM EncounterResults AS ER WHERE ER.PID1 = P.PatientID AND ER.Element = 'DIAGNOSIS') AS Dignosis,
(SELECT MaxEnteredDate FROM tmp_Encounter E WHERE E.EncounterType = 'BASELINE' AND E.PID = P.PatientID) AS DateBaselineVisit
FROM Patient AS P
INNER JOIN XRayResults AS X ON X.PatientID = P.PatientID
LEFT OUTER JOIN GeneXpertResults AS G ON G.PatientID = P.PatientID AND G.PatientID
LEFT OUTER JOIN tmp_Encounter AS S ON S.PID = P.PatientID AND S.EncounterType = 'SMEAR_POSITIVE'
WHERE X.XRayResults IN ('SUGGESTIVE OF TB', 'SUSPICIOUS OF TB')

-- Gene Xpert Positive Report
SELECT G.PatientID, P.MRNo, P.GPID, P.CHWID, G.SputumTestID AS Barcode, G.DrugResistance, G.DateTested,
	(SELECT MAX(E.DateEncounterEntered) FROM Encounter E WHERE E.EncounterType = 'BASELINE' AND E.DateEncounterEntered IS NOT NULL AND E.PID1 = P.PatientID) AS DateBaselineVisit,
	(SELECT COUNT(*) AS Counter FROM Encounter WHERE EncounterType = 'DRUG_ADM' AND PID1 = G.PatientID) AS DOTS FROM GeneXpertResults G
INNER JOIN Patient P ON P.PatientID = G.PatientID
WHERE G.IsPositive = 1 AND G.PatientID NOT IN (SELECT DISTINCT PatientID FROM SputumResults WHERE SmearResult <> 'NEGATIVE');

-- Smear Positive Report
SELECT DISTINCT P.PatientID, P.MRNo, P.GPID, P.CHWID, MAX(IFNULL(G.DrugResistance, '')) AS DrugResistance, MIN(S.DateTested) AS DateBaselineTested,
	(SELECT MAX(E.DateEncounterEntered) FROM Encounter E WHERE E.EncounterType = 'BASELINE' AND E.DateEncounterEntered IS NOT NULL AND E.PID1 = S.PatientID) AS DateBaselineVisit,
	(SELECT COUNT(*) AS Counter FROM Encounter WHERE EncounterType = 'DRUG_ADM' AND PID1 = S.PatientID) AS DOTS
FROM SputumResults S
INNER JOIN Patient P ON P.PatientID = S.PatientID
LEFT OUTER JOIN GeneXpertResults G ON G.PatientID = P.PatientID
WHERE S.SmearResult IS NOT NULL AND S.SmearResult <> 'NEGATIVE' 
AND P.PatientStatus <> 'CLOSED'
GROUP BY P.PatientID, P.MRNo, P.GPID, P.CHWID

-- Total verified forms filled by centre
SELECT PID2 AS GPID, COUNT(*) AS Total FROM Encounter
WHERE EncounterType = 'SUSPECTCON' AND DateEncounterStart BETWEEN $P{StartDate} AND $P{EndDate}
GROUP BY PID2;
SELECT GPID, (SELECT COUNT(*) AS Total FROM Encounter WHERE PID2 = GP.GPID AND EncounterType = 'SUSPECTCON' AND DateEncounterStart BETWEEN $P{StartDate} AND $P{EndDate}) ) AS Total FROM GP;

-- Total forms refused by centre
SELECT P.GPID, COUNT(*) AS Total FROM (SELECT * FROM Encounter WHERE EncounterType = 'REFUSAL') AS E
LEFT OUTER JOIN Patient P ON E.PID1 = P.PatientID 
GROUP BY P.GPID;

-- Total monitor verified forms
SELECT big.gpVal AS GPID, big.concval AS Value, COUNT(*) AS Total FROM Encounter AS e JOIN
(SELECT er1.EncounterID AS er1encid ,er1.PID1 AS er1id1 ,er1.PID2 as er1id2,er1.Element as gp,er1.Value as gpval,er2.Element as conc, er2.Value  as concval from
EncounterResults as er1 JOIN EncounterResults as er2 on er1.EncounterID=er2.EncounterID AND er1.PID1=er2.PID1 AND er1.PID2=er2.PID2 where er1.Element='GP_ID' AND
er2.Element='CONCLUSION') AS big
ON e.EncounterID=big.er1encid AND e.PID1=big.er1id1 AND e.PID2=big.er1id2 AND EncounterType='SUSPECTVER'
GROUP BY big.gpval, big.concval;

-- Total GP verified forms
SELECT P.GPID, ER.Value, COUNT(*) FROM Patient P
INNER JOIN Encounter E ON E.PID1 = P.PatientID AND E.EncounterType = 'SUSPECTVER' 
INNER JOIN EncounterResults ER ON ER.PID1 = E.PID1 AND ER.PID2 = E.PID2 AND ER.EncounterID = E.EncounterID AND ER.Element = 'CONCLUSION'
GROUP BY P.GPID, ER.Value;

-- Total baseline treatments
SELECT P.GPID, (SELECT COUNT(*) AS Total FROM Encounter WHERE EncounterType = 'BASELINE' AND PID2 = P.GPID) AS Total FROM GP P
GROUP BY P.GPID

-- Total refusals
SELECT G.GPID, (SELECT COUNT(*) FROM Encounter WHERE EncounterType = 'REFUSAL' AND PID1 IN (SELECT PatientID FROM Patient WHERE GPID = G.GPID)) AS Total FROM GP G
GROUP BY G.GPID

-- Total DOTs
SELECT G.GPID, (SELECT COUNT(*) FROM Encounter WHERE EncounterType = 'DRUG_ADM' AND PID1 IN (SELECT PatientID FROM Patient WHERE GPID = G.GPID)) AS Total FROM GP G
GROUP BY G.GPID

-- Total Smear Positives
SELECT G.GPID, (SELECT COUNT(DISTINCT PatientID) FROM SputumResults WHERE SmearResult <> 'NEGATIVE' AND SmearResult IS NOT NULL AND PatientID IN (SELECT PatientID FROM Patient WHERE GPID = G.GPID)) AS Total FROM GP G
GROUP BY G.GPID

-- Total confirmed/not confirmed
SELECT G.GPID, 
(SELECT COUNT(*) FROM Encounter WHERE EncounterType = 'SUSPECTCON' AND PID2 = G.GPID AND EncounterID IN (SELECT EncounterID FROM EncounterResults WHERE PID1 = Encounter.PID1 AND PID2 = Encounter.PID2 AND Element = 'CONCLUSION' AND Value = 'CONFIRMED')) AS Confirmed,
(SELECT COUNT(*) FROM Encounter WHERE EncounterType = 'SUSPECTCON' AND PID2 = G.GPID AND EncounterID IN (SELECT EncounterID FROM EncounterResults WHERE PID1 = Encounter.PID1 AND PID2 = Encounter.PID2 AND Element = 'CONCLUSION' AND Value = 'NOT CONFIRMED')) AS NotConfirmed FROM GP G
GROUP BY G.GPID;

-- Total baseline sputum submissions
SELECT G.GPID, (SELECT COUNT(*) AS Total FROM Encounter AS E, EncounterResults AS ER1, EncounterResults AS ER2, EncounterResults AS ER3, GP, Patient AS P
WHERE GP.GPID = G.GPID AND E.EncounterType = 'SPUTUM_COL' AND ER1.Element = 'COLLECTION_MONTH' AND ER1.Value = 'BASELINE' AND ER2.Element = 'SPUTUM_COLLECTED' AND ER2.Value = 'YES' AND ER3.Element = 'SUSPECT_SAMPLE' AND ER3.Value = '1' AND 
E.EncounterID = ER1.EncounterID AND E.PID1 = ER1.PID1 AND E.PID2 = ER2.PID2 AND E.EncounterID = ER2.EncounterID AND E.PID1 = ER2.PID1 AND E.PID1 = ER2.PID1 AND E.EncounterID = ER3.EncounterID AND E.PID1 = ER3.PID1 AND E.PID2 = ER3.PID2 AND 
ER1.EncounterID = ER2.EncounterID AND ER1.PID1 = ER2.PID1 AND ER1.PID2 = ER2.PID2 AND ER1.EncounterID = ER3.EncounterID AND ER1.PID1 = ER3.PID1 AND ER1.PID2 = ER3.PID2 AND ER2.EncounterID = ER3.EncounterID AND ER2.PID1 = ER3.PID1 AND ER2.PID2 = ER3.PID2 AND 
P.GPID = GP.GPID AND P.PatientID = ER1.PID1 AND P.PatientID = ER2.PID1 AND P.PatientID = ER3.PID1 AND P.PatientID = E.PID1) AS Total FROM GP G
GROUP BY G.GPID

-- Total verified forms filled
SELECT G.GPID, (SELECT COUNT(*) AS Total FROM Encounter AS E
INNER JOIN EncounterResults AS ER ON E.EncounterID = ER.EncounterID AND E.PID1 = ER.PID1 AND E.PID2 = ER.PID2
WHERE E.EncounterType='SUSPECTVER' AND ER.Element='GP_ID' AND ER.Value = G.GPID) AS Total FROM GP G
GROUP BY G.GPID

-- Total GP confirmed forms
SELECT G.GPID, (SELECT COUNT(*) AS Total FROM Encounter WHERE EncounterType = 'SUSPECTCON' AND PID2 = G.GPID) AS Total FROM GP G
GROUP BY G.GPID

-- Total identified
SELECT G.GPID, (SELECT COUNT(*) AS Total FROM Patient WHERE GPID = G.GPID) AS Total FROM GP G;