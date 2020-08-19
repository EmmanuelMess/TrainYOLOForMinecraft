.PHONY: default_target all clean

default_target: darknet/darknet darknet-data/yolov4.conv.137 $(MINECRAFT_FILES) $(TRAIN_FILES) $(TEST_FILES)
all: default_target

darknet-data: run/darknet-data
	cp -r run/darknet-data .
    
IMAGES = $(patsubst obj.raw/%.png,obj/%.jpg,$(wildcard darknet-data/data/obj.raw/*.png))

darknet-data/data/obj/%.jpg: darknet-data/data/obj.raw/%.png
	convert $< $@

yolov4.conv.137: 
	curl -LJO https://github.com/AlexeyAB/darknet/releases/download/darknet_yolo_v3_optimal/yolov4.conv.137

darknet-data/yolov4.conv.137: darknet-data yolov4.conv.137
	cp yolov4.conv.137 darknet-data

darknet/darknet:
	cd darknet && sh build.sh

MINECRAFT_FILES = darknet-data/data/obj.data darknet-data/data/obj.names darknet-data/data/test.txt darknet-data/data/train.txt
TRAIN_FILES = $(addprefix darknet-data/, $(file < darknet-data/data/train.txt))
TEST_FILES = $(addprefix darknet-data/, $(file < darknet-data/data/test.txt))

run: darknet/darknet darknet-data/yolov4.conv.137 $(MINECRAFT_FILES) $(IMAGES) $(TRAIN_FILES) $(TEST_FILES)
	cd darknet && ./darknet detector train data/obj.data yolo-obj.cfg yolov4.conv.137
	
clean:
	-rm -rf darknet-data
	-rm -rf darknet/build_release
	-rm darknet/darknet
