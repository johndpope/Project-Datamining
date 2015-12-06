INPUT='/home/ankit/studies/uf/third_sem/datamining/project/genres/war'
MALLETFOLDER='/home/ankit/studies/uf/third_sem/datamining/project/output/war/mallet/'
KeysFOLDER='/home/ankit/studies/uf/third_sem/datamining/project/output/war/'
for file in $INPUT/*
do
  FILENAME=$(echo $file| cut -d'/' -f 11)
  echo $file
  FILENAME=$(echo $FILENAME| cut -d'.' -f 1)
  echo $FILENAME
  bin/mallet import-file --input $file --output $MALLETFOLDER$FILENAME.mallet --keep-sequence --remove-stopwords
  bin/mallet train-topics  --input $MALLETFOLDER$FILENAME.mallet --num-topics 100 --optimize-interval 20  --output-topic-keys $KeysFOLDER$FILENAME.txt 
done
