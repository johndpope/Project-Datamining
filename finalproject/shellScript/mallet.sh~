###########################Other Approach Script###############################

INPUT='/home/ankit/studies/uf/third_sem/datamining/project/genres'
MALLETFOLDER='/home/ankit/studies/uf/third_sem/datamining/project/otherApproach/'
KeysFOLDER='/home/ankit/studies/uf/third_sem/datamining/project/otherApproach/output/'
for file in $INPUT/*
do
  FILENAME=$(echo $file| cut -d'/' -f 10)
  echo $file
  echo $FILENAME
  bin/mallet import-dir --input $file --output $MALLETFOLDER$FILENAME.mallet --keep-sequence --remove-stopwords
  bin/mallet train-topics  --input $MALLETFOLDER$FILENAME.mallet --num-topics 100 --optimize-interval 20  --output-topic-keys $KeysFOLDER$FILENAME.txt 
done
