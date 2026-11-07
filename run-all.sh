#!/usr/bin/env bash
# Runs every experiment and saves the outputs in ./results (paste them into the report).
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p results
./run.sh JavaSerializationLab       | tee results/task2_2_JavaSerializationLab.txt
./run.sh Task23Experiments          | tee results/task2_3_experiments.txt
./run.sh StaticFieldCrossJvmDemo write | tee results/task2_3_static_write.txt
./run.sh StaticFieldCrossJvmDemo read  | tee results/task2_3_static_read.txt
./run.sh SchemaMismatchDemo         | tee results/task1_2_schema_mismatch.txt
./run.sh ProtobufLab                | tee results/task3_2_ProtobufLab.txt
./run.sh SizeComparison             | tee results/report_q2_size_comparison.txt
./run.sh ProtobufRenameExperiment   | tee results/report_q2_rename_experiment.txt
echo
echo "Done. Outputs are in ./results"
