#!/bin/bash

rm ./output/*

./compare.sh -f1 ./src/test/resources/3.0.6SchemaBom.xml -f2 ./src/test/resources/5.0.1SchemaBom.xml -o ./output/output -f xml -ob ./output/newDiffBom -t ./output/quickOutput
