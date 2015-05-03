/* NEW REPORTS FROM ERUM */

-- Follow-up Progress Report
select PatientID, GPID, CHWID, MRNo, Source, BaselineDiagnosis, DateBaselineVisit, BaselineTestDate, PatientStatus, Category, Visit1Date, Smear1Date, Smear1Result,
Visit2Date, (case when Visit2Date is null and datediff(curdate(), DateBaselineVisit) >= 54 then 'YES' else null end) as Visit2Due, Smear2Date, Smear2Result,
Visit3Date, (case when Visit3Date is null and datediff(curdate(), DateBaselineVisit) >= 84 then 'YES' else null end) as Visit3Due, Smear3Date, Smear3Result,
Visit4Date, Smear4Date, Smear4Result,
Visit5Date, (case when Visit5Date is null and datediff(curdate(), DateBaselineVisit) >= 144 then 'YES' else null end) as Visit5Due, Smear5Date, Smear5Result,
Visit6Date, Smear6Date, Smear6Result,
Visit7Date, (case when Visit7Date is null and datediff(curdate(), DateBaselineVisit) >= 174 then 'YES' else null end) as Visit7Due, Smear7Date, Smear7Result,
Visit8Date, Smear8Date, Smear8Result, Visit9Date, Smear9Date, Smear9Result, Visit10Date, Smear10Date, Smear10Result from tbreach_rpt._FollowupProgress;

-- Daily Summary
drop table if exists tbreach_rpt._DailySummary;
create table if not exists tbreach_rpt._DailySummary
select G.GPID, 
(select count(distinct PatientID) from tbreach_rpt.Patient where GPID = G.GPID and DateRegistered between StartDate and EndDate) as TotalScreened, 
(select count(distinct S.PatientID) from tbreach_rpt.SputumResults as S inner join Patient as P using (PatientID) where P.GPID = G.GPID and S.Month = 0 and S.Remarks = 'VERIFIED' and S.DateSubmitted between StartDate and EndDate) as TotalBaselineSputum, 
(select count(distinct X.PatientID) from tbreach_rpt.XRayResults as X inner join Patient as P using (PatientID) where P.GPID = G.GPID and X.XRayDate between StartDate and EndDate) as TotalXRay, 
(select count(distinct S.PatientID) from tbreach_rpt.SputumResults as S inner join Patient as P using (PatientID) where P.GPID = G.GPID and S.Month = 0 and S.SmearResult is not null and S.SmearResult <> 'NEGATIVE' and S.DateTested between StartDate and EndDate) as TotalSmearPositive, 
(select count(distinct GXP.PatientID) from tbreach_rpt.GeneXpertResults as GXP inner join Patient as P using (PatientID) where P.GPID = G.GPID and GXP.IsPositive = 1 and GXP.DateTested between StartDate and EndDate) as TotalGXPPositive, 
(select count(distinct PID1) from tbreach_rpt.Encounter inner join tbreach_rpt.EncounterResults using (EncounterID, PID1, PID2) where Encounter.PID2 = G.GPID and EncounterType = 'CDF' and Element = 'DIAGNOSIS' and Value in ('SMEAR -VE PULMONARY TB', 'SMEAR +VE PULMONARY TB') and Encounter.DateEncounterEntered between StartDate and EndDate) as TotalAdultPTBCases, 
(select count(distinct PID1) from tbreach_rpt.Encounter inner join tbreach_rpt.EncounterResults using (EncounterID, PID1, PID2) where Encounter.PID2 = G.GPID and EncounterType = 'CDF' and Element = 'DIAGNOSIS' and Value = 'EP-TB' and Encounter.DateEncounterEntered between StartDate and EndDate) as TotalAdultEPTBCases, 
(select count(distinct PID1) from tbreach_rpt.Encounter inner join tbreach_rpt.EncounterResults using (EncounterID, PID1, PID2) where Encounter.PID2 = G.GPID and EncounterType = 'PAED_DIAG' and Element = 'DIAGNOSIS' and Value in ('SMEAR -VE PULMONARY TB', 'SMEAR +VE PULMONARY TB') and Encounter.DateEncounterEntered between StartDate and EndDate) as TotalPaediatricPTBCases, 
(select count(distinct PID1) from tbreach_rpt.Encounter inner join tbreach_rpt.EncounterResults using (EncounterID, PID1, PID2) where Encounter.PID2 = G.GPID and EncounterType = 'PAED_DIAG' and Element = 'DIAGNOSIS' and Value = 'EP-TB' and Encounter.DateEncounterEntered between StartDate and EndDate) as TotalPaediatricEPTBCases, 
(select count(distinct PID1) from tbreach_rpt.Encounter inner join tbreach_rpt.EncounterResults using (EncounterID, PID1, PID2) where Encounter.PID2 = G.GPID and EncounterType = 'BASELINE' and Encounter.DateEncounterEntered between StartDate and EndDate) as TotalBaselineVisits from tbreach_rpt.GP G;


-- All PID with no CXR in month 0 and Sputum result is not positive
select P.PatientID, P.GPID, P.PatientStatus, P.CHWID, P.MonitorID from tbreach_rpt.Patient P
inner join tbreach_rpt.SputumResults as S using (PatientID)
where P.DiseaseConfirmed = 1 and S.Month = 0 and S.SmearResult = 'NEGATIVE'
and not exists (select * from tbreach_rpt.XRayResults where PatientID = P.PatientID)
order by CHWID;

-- All PIDs where Smear Result = Positive but no Baseline form
select P.PatientID, P.GPID, P.PatientStatus, S.DateTested, P.CHWID, P.MonitorID from tbreach_rpt.Patient P
inner join tbreach_rpt.SputumResults as S using (PatientID)
where P.DiseaseConfirmed = 1 and S.Month = 0 and S.SmearResult is not null and S.SmearResult <> 'NEGATIVE'
and not exists (select * from tbreach_rpt.tmp_Encounter where EncounterType = 'BASELINE' and PID = P.PatientID)
order by CHWID;

-- List of all Adult (Age > 15) cases having no SS +ve and no GXP +ve and no pending Smear and no pending GXP and X-Ray result as Suggestive having Diagnosis not done yet
select P.PatientID, P.GPID, P.PatientStatus, P.CHWID, P.MonitorID, X.XRayDate, X.XRayResults from tbreach_rpt.Patient P
inner join tbreach_rpt.Person Pr on P.PatientID = Pr.PID 
inner join tbreach_rpt.XRayResults as X using (PatientID)
where year(P.DateRegistered) - year(Pr.DOB) > 15 and X.XRayResults like '%SUGGESTIVE%'
and not exists (select * from tbreach_rpt.SputumResults where PatientID = P.PatientID and (SmearResult is null or SmearResult <> 'NEGATIVE'))
and not exists (select * from tbreach_rpt.GeneXpertResults where PatientID = P.PatientID and (DateTested is null or IsPositive = 1))
and not exists (select * from tbreach_rpt.tmp_Encounter where EncounterType = 'CDF' and PID = P.PatientID)
order by CHWID;

-- List of all EPTB (from Suspect screening form) Suspects with no pending SS and no pending GXP and no SS +Ve and no GXP +Ve having Diagnosis not done yet
select P.PatientID, P.GPID, P.PatientStatus, P.CHWID from tbreach_rpt.Patient P 
inner join (select E.PID1 from tbreach_rpt.Encounter E inner join tbreach_rpt.EncounterResults Er using (EncounterID, PID1, PID2) where E.EncounterType = 'SUSPECT_ID' and Er.Element = 'EPTB_SUSPECT' and Er.Value = 'YES') T on P.PatientID = T.PID1 
where not exists (select * from tbreach_rpt.SputumResults where PatientID = P.PatientID and (SmearResult is null or SmearResult <> 'NEGATIVE'))
and not exists (select * from tbreach_rpt.GeneXpertResults where PatientID = P.PatientID and (DateTested is null or IsPositive = 1))
and not exists (select * from tbreach_rpt.tmp_Encounter where EncounterType in ('CDF', 'PAED_DIAG') and PID = P.PatientID)
order by CHWID;

-- List of Adult (Age > 15) Suspects having less than 3 sputum attempts and less than 2 verified samples 
select P.PatientID, P.GPID, P.CHWID, P.MonitorID, 
(select count(*) from tbreach_rpt.Encounter inner join tbreach_rpt.EncounterResults using (EncounterID, PID1, PID2) where PID1 = P.PatientID and EncounterType = 'SPUTUM_COL' and Element = 'SPUTUM_COLLECTED') AS Attempts, 
(select count(SputumTestID) from tbreach_rpt.SputumResults where PatientID = P.PatientID and Month = 0 and Remarks not like 'REJECTED%') as Verified, 
(select count(SputumTestID) from tbreach_rpt.SputumResults where PatientID = P.PatientID and Month = 0 and Remarks not like 'REJECTED%' and SmearResult is null) as Pending, 
(select count(SputumTestID) from tbreach_rpt.SputumResults where PatientID = P.PatientID and Month = 0 and SmearResult = 'NEGATIVE') as SmearNegative, 
(select count(SputumTestID) from tbreach_rpt.SputumResults where PatientID = P.PatientID and Month = 0 and Remarks like 'REJECTED%') as Rejected 
from tbreach_rpt.Patient P 
inner join tbreach_rpt.Person Pr on P.PatientID = Pr.PID 
where P.PatientStatus <> 'CLOSED' and P.DiseaseConfirmed = 0 and year(P.DateRegistered) - year(Pr.DOB) > 15 
having Attempts < 3 and Verified < 2;

-- CAT I REPORTS

-- Baseline Forms
-- 1_List of Patients whose patient type is not NEW
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.PatientStatus, P.Regimen, ER.Value AS PatientType from Encounter E
inner join EncounterResults ER using (EncounterID, PID1, PID2) 
inner join Patient P on P.PatientID = E.PID1
where P.DiseaseCategory = 'CAT I' and P.DiseaseConfirmed = 1 and E.EncounterType = 'BASELINE' and ER.Element = 'PATIENT_TYPE' and ER.Value <> 'NEW';

-- 2_List of patients whose treatment is not RHZE
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.PatientStatus, ER.Value AS BaselineRegimen from Encounter E
inner join EncounterResults ER using (EncounterID, PID1, PID2) 
inner join Patient P on P.PatientID = E.PID1
where P.DiseaseCategory = 'CAT I' and P.DiseaseConfirmed = 1 and E.EncounterType = 'BASELINE' and ER.Element = 'REGIMEN' and ER.Value <> 'RHZE';

-- 3_List of patients whose phase of treatment is continuous
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.PatientStatus, ER.Value AS TreatmentPhase from Encounter E
inner join EncounterResults ER using (EncounterID, PID1, PID2) 
inner join Patient P on P.PatientID = E.PID1
where P.DiseaseCategory = 'CAT I' and P.DiseaseConfirmed = 1 and E.EncounterType = 'BASELINE' and ER.Element = 'TREATMENT_PHASE' and ER.Value = 'CONTINUOUS';

-- Followup Forms
-- 4_List of patients who are 2m smear positive and on continuous phase of treatment
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.PatientStatus, P.TreatmentPhase, S.DateTested AS M2SmearDate, S.SmearResult from SputumResults S
inner join Patient P using (PatientID)
where P.DiseaseCategory = 'CAT I' and P.DiseaseConfirmed = 1 and S.Month = 2 and S.SmearResult is not null and S.SmearResult <> 'NEGATIVE' and P.TreatmentPhase = 'CONTINUOUS';

-- 5_List of patients who are 2m smear negative and on intensive phase of treatment
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.PatientStatus, P.TreatmentPhase, S.DateTested AS M2SmearDate, S.SmearResult from SputumResults S
inner join Patient P using (PatientID)
where P.DiseaseCategory = 'CAT I' and P.DiseaseConfirmed = 1 and S.Month = 2 and S.SmearResult is not null and S.SmearResult = 'NEGATIVE' and P.TreatmentPhase = 'INTENSIVE';

-- 6_List of patients who are continuous phase of treatment and receiving HRZE or HRZES
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, P.Regimen from Patient P
where P.DiseaseCategory = 'CAT I' and P.DiseaseConfirmed = 1 and P.TreatmentPhase = 'CONTINUOUS' and P.Regimen in ('RHZE', 'RHZES');

-- 7_List of patients who are intensive phase of treatment and receiving HE or HRZES
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, P.Regimen from Patient P
where P.DiseaseCategory = 'CAT I' and P.DiseaseConfirmed = 1 and P.TreatmentPhase = 'INTENSIVE' and P.Regimen in ('HE', 'RHZES');

-- 8_List of patients on continuous phase of treatment started before 2m of treatment
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT I' and P.DiseaseConfirmed = 1 and P.TreatmentPhase = 'CONTINUOUS' and datediff(curdate(), E.MaxEnteredDate) < (7 * 8);

-- 9_List of patients who have never had any followup visit after 30 days of Baseline
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT I' and P.DiseaseConfirmed = 1 and datediff(curdate(), E.MaxEnteredDate) > (7 * 5);

-- 10_List of patients who have no 7m smear result or a smear positive and have been classified as Cured
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.DiseaseSite, P.TreatmentPhase, P.PatientStatus, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT I' and P.PatientStatus = 'CURED' and P.DiseaseSite = 'PULMONARY' 
and P.PatientID not in (select PatientID from SputumResults where Month = 7 and SmearResult = 'NEGATIVE');

-- 11_List of patients who have a 7m smear negative result and have been classified as treatment completed
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.PatientStatus, S.DateTested AS M7SmearDate, S.SmearResult, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
inner join SputumResults S using (PatientID) 
where P.DiseaseCategory = 'CAT I' and S.Month = 7 and S.SmearResult is not null and S.SmearResult = 'NEGATIVE'
and exists (select * from tmp_EncounterResults where PID1 = P.PatientID and EncounterType = 'END_FOL' and Element = 'REASON' and Value = 'TX COMPLETED');

-- 12_List of patients classified as smear negative PTP or EPTB at baseline with a final outcome of Cured
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.PatientStatus, E.Value AS BaselineDiagnosis from Patient P
inner join tmp_EncounterResults E on P.PatientID = E.PID1
where P.DiseaseCategory = 'CAT I' and P.PatientStatus = 'CURED' and E.EncounterType = 'BASELINE' and E.Element = 'DIAGNOSIS' and E.Value in ('SMEAR -VE PULMONARY TB', 'EP-TB');

-- 13_List of patients who have received less than 8 months of treatment but have a final outcome of treatment completed or cured
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, P.PatientStatus, ER.Value as EndOfFollowupReason, E.MaxEnteredDate as Baseline, ER.EnteredDate as EndOfFollowupDate from tmp_EncounterResults ER
inner join Patient P on ER.PID1 = P.PatientID
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT I' and ER.EncounterType = 'END_FOL' and ER.Element = 'REASON' and ER.Value IN ('TX COMPLETED', 'CURED') and datediff(ER.EnteredDate, E.MaxEnteredDate) < (7 * 32);

-- 14_List of patients who have received less than 5 months of treatment and have been classified as treatment failure
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, P.PatientStatus, E.MaxEnteredDate as Baseline from tmp_EncounterResults ER
inner join Patient P on ER.PID1 = P.PatientID
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT I' and ER.EncounterType = 'END_FOL' and ER.Element = 'REASON' and ER.Value = 'FAILURE' and datediff(E.MaxEnteredDate, ER.EnteredDate) < (7 * 20);

-- 15_List of patients who have not had their 2m of followup
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, P.PatientStatus, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT I' and P.DiseaseConfirmed = 1  
and datediff(curdate(), E.MaxEnteredDate) > (7 * 9)
and P.PatientID not in (select E2.PID1 from Encounter E2 inner join EncounterResults ER2 using (EncounterID, PID1, PID2) where E2.PID1 = P.PatientID and E2.EncounterType = 'FOLLOW_UP' and ER2.Element = 'MONTH' and ER2.Value = '2');

-- 16_List of (P-TB) patients who have not had their 2m sputum collection attempted
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.DiseaseSite, P.TreatmentPhase, P.PatientStatus, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT I' and P.DiseaseConfirmed = 1 and P.DiseaseSite = 'PULMONARY'
and datediff(curdate(), E.MaxEnteredDate) > (7 * 9)
and P.PatientID not in (select E2.PID1 from Encounter E2 inner join EncounterResults ER2 using (EncounterID, PID1, PID2) where E2.PID1 = P.PatientID and E2.EncounterType = 'SPUTUM_COL' and ER2.Element = 'MONTH' and ER2.Value = '2');

-- 17_List of patient who have not had their 5m followup
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, P.PatientStatus, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT I' and P.DiseaseConfirmed = 1 
and datediff(curdate(), E.MaxEnteredDate) > (7 * 21)
and P.PatientID not in (select E2.PID1 from Encounter E2 inner join EncounterResults ER2 using (EncounterID, PID1, PID2) where E2.PID1 = P.PatientID and E2.EncounterType = 'FOLLOW_UP' and ER2.Element = 'MONTH' and ER2.Value = '5');

-- 18_List of (P-TB) patients who have not had their 5m sputum collection attempted (exclude smear negative patients who are smear negative month 2-new WHO guidelines)
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.DiseaseSite, P.TreatmentPhase, P.PatientStatus, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT I' and P.DiseaseConfirmed = 1 and P.DiseaseSite = 'PULMONARY'
and datediff(curdate(), E.MaxEnteredDate) > (7 * 21)
and P.PatientID not in (select E2.PID1 from Encounter E2 inner join EncounterResults ER2 using (EncounterID, PID1, PID2) where E2.PID1 = P.PatientID and E2.EncounterType = 'SPUTUM_COL' and ER2.Element = 'MONTH' and ER2.Value = '5')
and P.PatientID not in (select PatientID from SputumResults where Month = 2 and SmearResult = 'NEGATIVE');

-- 19_List of patient who have not had their 7m followup
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, P.PatientStatus, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT I' and P.DiseaseConfirmed = 1 
and P.PatientID not in (select E2.PID1 from Encounter E2 inner join EncounterResults ER2 using (EncounterID, PID1, PID2) where E2.PID1 = P.PatientID and E2.EncounterType = 'FOLLOW_UP' and ER2.Element = 'MONTH' and ER2.Value = '7')
and datediff(curdate(), E.MaxEnteredDate) > (7 * 29);

-- 20_List of (P-TB) patients who have not had their 5m sputum collection attempted (exclude smear negative patients who are smear negative month 2-new WHO guidelines)
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.DiseaseSite, P.TreatmentPhase, P.PatientStatus, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT I' and P.DiseaseConfirmed = 1 and P.DiseaseSite = 'PULMONARY'
and datediff(curdate(), E.MaxEnteredDate) > (7 * 29)
and P.PatientID not in (select E2.PID1 from Encounter E2 inner join EncounterResults ER2 using (EncounterID, PID1, PID2) where E2.PID1 = P.PatientID and E2.EncounterType = 'SPUTUM_COL' and ER2.Element = 'MONTH' and ER2.Value = '7')
and P.PatientID not in (select PatientID from SputumResults where Month = 2 and SmearResult = 'NEGATIVE');

-- 21_List of patients who are 2m smear positive whose 3m sputum was not attempted
select S.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, P.PatientStatus, E.MaxEnteredDate as Baseline from Patient P
inner join SputumResults S on P.PatientID = S.PatientID
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT I' and P.DiseaseConfirmed = 1 and S.Month = 2 and S.SmearResult is not null and S.SmearResult <> 'NEGATIVE'
and datediff(curdate(), E.MaxEnteredDate) > (7 * 13)
and P.PatientID not in (select E2.PID1 from Encounter E2 inner join EncounterResults ER2 using (EncounterID, PID1, PID2) where E2.PID1 = P.PatientID and E2.EncounterType = 'SPUTUM_COL' and ER2.Element = 'MONTH' and ER2.Value = '3');


-- CAT II REPORTS

-- Baseline Forms
-- 1_List of Patients whose patient type is NEW
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.PatientStatus, P.Regimen, ER.Value AS PatientType from Encounter E
inner join EncounterResults ER using (EncounterID, PID1, PID2) 
inner join Patient P on P.PatientID = E.PID1
where P.DiseaseCategory = 'CAT II' and P.DiseaseConfirmed = 1 and E.EncounterType = 'BASELINE' and ER.Element = 'PATIENT_TYPE' and ER.Value = 'NEW';

-- 2_List of patients whose treatment is everything other than HRZES
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.PatientStatus, ER.Value AS BaselineRegimen from Encounter E
inner join EncounterResults ER using (EncounterID, PID1, PID2) 
inner join Patient P on P.PatientID = E.PID1
where P.DiseaseCategory = 'CAT II' and P.DiseaseConfirmed = 1 and E.EncounterType = 'BASELINE' and ER.Element = 'REGIMEN' and ER.Value <> 'RHZES';

-- 3_List of patients whose phase of treatment is continuous
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.PatientStatus, ER.Value AS TreatmentPhase from Encounter E
inner join EncounterResults ER using (EncounterID, PID1, PID2) 
inner join Patient P on P.PatientID = E.PID1
where P.DiseaseCategory = 'CAT II' and P.DiseaseConfirmed = 1 and E.EncounterType = 'BASELINE' and ER.Element = 'TREATMENT_PHASE' and ER.Value = 'CONTINUOUS';

-- Followup Forms
-- 4_List of patients who are 3m smear positive and on continuous phase of treatment
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.PatientStatus, ER.Value AS TreatmentPhase, S.SmearResult as M3SmearResult from Encounter E
inner join EncounterResults ER using (EncounterID, PID1, PID2) 
inner join Patient P on P.PatientID = E.PID1
inner join SputumResults S on P.PatientID = S.PatientID 
where P.DiseaseCategory = 'CAT II' and P.DiseaseConfirmed = 1 and E.EncounterType = 'BASELINE' and ER.Element = 'TREATMENT_PHASE' and ER.Value = 'CONTINUOUS' and S.Month = 3 and S.SmearResult is not null and S.SmearResult <> 'NEGATIVE';

-- 5_List of patients who are 3m smear negative and on intensive phase of treatment
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.PatientStatus, ER.Value AS TreatmentPhase, S.SmearResult as M3SmearResult from Encounter E
inner join EncounterResults ER using (EncounterID, PID1, PID2) 
inner join Patient P on P.PatientID = E.PID1
inner join SputumResults S on P.PatientID = S.PatientID 
where P.DiseaseCategory = 'CAT II' and P.DiseaseConfirmed = 1 and E.EncounterType = 'BASELINE' and ER.Element = 'TREATMENT_PHASE' and ER.Value = 'INTENSIVE' and S.Month = 3 and S.SmearResult = 'NEGATIVE';

-- 6_List of patients who are continuous phase of treatment and receiving HRZE or HRZES
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, P.Regimen from Patient P
where P.DiseaseCategory = 'CAT II' and P.DiseaseConfirmed = 1 and P.TreatmentPhase = 'CONTINUOUS' and P.Regimen in ('RHZE', 'RHZES');

-- 7_List of patients who are intensive phase of treatment and receiving HE or HRZE
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, P.Regimen from Patient P
where P.DiseaseCategory = 'CAT II' and P.DiseaseConfirmed = 1 and P.TreatmentPhase = 'INTENSIVE' and P.Regimen in ('HE', 'RHZE');

-- 8_List of patients who have never had any follow-up visit after 30 days of Baseline
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT II' and P.DiseaseConfirmed = 1 and datediff(curdate(), E.MaxEnteredDate) > (7 * 5);

-- 9_List of patients on continuous phase of treatment started before 3 months of treatment
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT II' and P.DiseaseConfirmed = 1 and P.TreatmentPhase = 'CONTINUOUS' and datediff(curdate(), E.MaxEnteredDate) < (7 * 12);

-- 10_List of patients who have no 7 month smear result or a smear positive and have been classified as ‘cured’
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.DiseaseSite, P.TreatmentPhase, P.PatientStatus, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT II' and P.PatientStatus = 'CURED' and P.DiseaseSite = 'PULMONARY'
and P.PatientID not in (select PatientID from SputumResults where Month = 7 and SmearResult = 'NEGATIVE');

-- 11_List of patients who have a 7 month smear negative result and have been classified as treatment completed
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.DiseaseSite, P.PatientStatus, S.DateTested AS M7SmearDate, S.SmearResult, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
inner join SputumResults S using (PatientID) 
where P.DiseaseCategory = 'CAT II' and S.Month = 7 and S.SmearResult is not null and S.SmearResult = 'NEGATIVE'
and exists (select * from tmp_EncounterResults where PID1 = P.PatientID and EncounterType = 'END_FOL' and Element = 'REASON' and Value = 'TX COMPLETED');

-- 12_List of patients classified as smear negative PTP or EPTB at baseline with a final outcome of cured
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.PatientStatus, E.Value AS BaselineDiagnosis from Patient P
inner join tmp_EncounterResults E on P.PatientID = E.PID1
where P.DiseaseCategory = 'CAT II' and P.PatientStatus = 'CURED' and E.EncounterType = 'BASELINE' and E.Element = 'DIAGNOSIS' and E.Value in ('SMEAR -VE PULMONARY TB', 'EP-TB');

-- 13_List of patients who have received less than 8 months of treatment but have a final outcome of treatment completed or cured
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, P.PatientStatus, ER.Value as EndOfFollowupReason, E.MaxEnteredDate as Baseline, ER.EndDate as EndOfFollowupDate from tmp_EncounterResults ER
inner join Patient P on ER.PID1 = P.PatientID
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT II' and ER.EncounterType = 'END_FOL' and ER.Element = 'REASON' and ER.Value IN ('TX COMPLETED', 'CURED') and datediff(ER.EnteredDate, E.MaxEnteredDate) < (7 * 32);

-- 14_List of patients who have received less than 5 months of treatment and have been classified as treatment failure
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, P.PatientStatus, E.MaxEnteredDate as Baseline from tmp_EncounterResults ER
inner join Patient P on ER.PID1 = P.PatientID
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT I' and ER.EncounterType = 'END_FOL' and ER.Element = 'REASON' and ER.Value = 'FAILURE' and datediff(E.MaxEnteredDate, ER.EnteredDate) < (7 * 20);

-- 15_List of patients who have not had their 3 month of follow-up visit
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, P.PatientStatus, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT II' and P.DiseaseConfirmed = 1 
and datediff(curdate(), E.MaxEnteredDate) > (7 * 13)
and P.PatientID not in (select E2.PID1 from Encounter E2 inner join EncounterResults ER2 using (EncounterID, PID1, PID2) where E2.PID1 = P.PatientID and E2.EncounterType = 'FOLLOW_UP' and ER2.Element = 'MONTH' and ER2.Value = '3');

-- 16_List of (P-TB) patients who have not had their month 3 sputum collection attempted
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.DiseaseSite, P.TreatmentPhase, P.PatientStatus, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT II' and P.DiseaseConfirmed = 1 and P.DiseaseSite = 'PULMONARY'
and datediff(curdate(), E.MaxEnteredDate) > (7 * 13)
and P.PatientID not in (select E2.PID1 from Encounter E2 inner join EncounterResults ER2 using (EncounterID, PID1, PID2) where E2.PID1 = P.PatientID and E2.EncounterType = 'SPUTUM_COL' and ER2.Element = 'MONTH' and ER2.Value = '3');

-- 17_List of patient who have not had their 5 month follow-up visit
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, P.PatientStatus, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT II' and P.DiseaseConfirmed = 1 
and datediff(curdate(), E.MaxEnteredDate) > (7 * 21)
and P.PatientID not in (select E2.PID1 from Encounter E2 inner join EncounterResults ER2 using (EncounterID, PID1, PID2) where E2.PID1 = P.PatientID and E2.EncounterType = 'FOLLOW_UP' and ER2.Element = 'MONTH' and ER2.Value = '5');

-- 18_List of (P-TB) patients who have not had their month 5 sputum collection attempted (exclude smear negative patients who are smear negative month 3-new WHO guidelines)
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.DiseaseSite, P.TreatmentPhase, P.PatientStatus, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT II' and P.DiseaseConfirmed = 1 and P.DiseaseSite = 'PULMONARY'
and datediff(curdate(), E.MaxEnteredDate) > (7 * 21)
and P.PatientID not in (select E2.PID1 from Encounter E2 inner join EncounterResults ER2 using (EncounterID, PID1, PID2) where E2.PID1 = P.PatientID and E2.EncounterType = 'SPUTUM_COL' and ER2.Element = 'MONTH' and ER2.Value = '5')
and P.PatientID not in (select PatientID from SputumResults where Month = 3 and SmearResult = 'NEGATIVE');

-- 19_List of patients who have not had their 7 month follow-up visit
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.TreatmentPhase, P.PatientStatus, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT II' and P.DiseaseConfirmed = 1 
and datediff(curdate(), E.MaxEnteredDate) > (7 * 29)
and P.PatientID not in (select E2.PID1 from Encounter E2 inner join EncounterResults ER2 using (EncounterID, PID1, PID2) where E2.PID1 = P.PatientID and E2.EncounterType = 'FOLLOW_UP' and ER2.Element = 'MONTH' and ER2.Value = '7');

-- 20_List of (P-TB) patients who have not had their month 7 sputum collection attempted (exclude smear negative patients who are smear negative month 3-new WHO guidelines)
select P.PatientID, P.CHWID, P.GPID, P.MRNo, P.DiseaseCategory, P.DiseaseSite, P.TreatmentPhase, P.PatientStatus, E.MaxEnteredDate as Baseline from Patient P
inner join tmp_Encounter E on P.PatientID = E.PID and E.EncounterType = 'BASELINE'
where P.DiseaseCategory = 'CAT II' and P.DiseaseConfirmed = 1 and P.DiseaseSite = 'PULMONARY'
and datediff(curdate(), E.MaxEnteredDate) > (7 * 29)
and P.PatientID not in (select E2.PID1 from Encounter E2 inner join EncounterResults ER2 using (EncounterID, PID1, PID2) where E2.PID1 = P.PatientID and E2.EncounterType = 'SPUTUM_COL' and ER2.Element = 'MONTH' and ER2.Value = '7')
and P.PatientID not in (select PatientID from SputumResults where Month = 3 and SmearResult = 'NEGATIVE');


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
WHERE IFNULL(S.SmearResult, '') <> '' AND S.SmearResult <> 'NEGATIVE' 
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