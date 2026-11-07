@echo off
setlocal
cd /d "%~dp0"
if not exist results mkdir results
call run.bat JavaSerializationLab          > results\task2_2_JavaSerializationLab.txt 2>&1
call run.bat Task23Experiments             > results\task2_3_experiments.txt 2>&1
call run.bat StaticFieldCrossJvmDemo write > results\task2_3_static_write.txt 2>&1
call run.bat StaticFieldCrossJvmDemo read  > results\task2_3_static_read.txt 2>&1
call run.bat SchemaMismatchDemo            > results\task1_2_schema_mismatch.txt 2>&1
call run.bat ProtobufLab                   > results\task3_2_ProtobufLab.txt 2>&1
call run.bat SizeComparison                > results\report_q2_size_comparison.txt 2>&1
call run.bat ProtobufRenameExperiment      > results\report_q2_rename_experiment.txt 2>&1
echo Done. Outputs are in the results folder (open them with a UTF-8 editor such as VS Code).
