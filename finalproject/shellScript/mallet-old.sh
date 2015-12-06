INPUT='/home/ankit/studies/uf/third_sem/datamining/project/genres/adventure2'
MALLETFOLDER='/home/ankit/studies/uf/third_sem/datamining/project/genres/output/mallet/'
KeysFOLDER='/home/ankit/studies/uf/third_sem/datamining/project/genres/output/keys/'
StateFOLDER='/home/ankit/studies/uf/third_sem/datamining/project/genres/output/state/'
TopicsFOLDER='/home/ankit/studies/uf/third_sem/datamining/project/genres/output/topics/'
for file in $INPUT/*
do
  FILENAME=$(echo $file| cut -d'/' -f 11)
  echo $file
  FILENAME=$(echo $FILENAME| cut -d'.' -f 1)
  echo $FILENAME
  bin/mallet import-file --input $file --output $MALLETFOLDER$FILENAME.mallet --keep-sequence --remove-stopwords
  bin/mallet train-topics  --input $MALLETFOLDER$FILENAME.mallet --num-topics 100 --output-state $StateFOLDER$FILENAME.gz --output-topic-keys $KeysFOLDER$FILENAME.txt --output-doc-topics $TopicsFOLDER$FILENAME.txt 
done
