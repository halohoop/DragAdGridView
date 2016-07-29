# DragAdGridView
![Markdown](http://i4.piimg.com/8359/ed78039f1d3a024a.gif)
#Usage
###### 0.compile 'com.halohoop:dragadgridview:1.0.0'

###### 1.set the widget to your xml file;
-- --
	xmlns:halohoop="http://schemas.android.com/apk/res-auto"
	...
	...
    <com.halohoop.draggableadgridview.views.DraggableAdGridView
    	android:id="@+id/dgv"
    	android:layout_width="match_parent"
    	android:layout_height="match_parent"
   		android:listSelector="@android:color/transparent"
    	android:numColumns="4"
    	halohoop:adbar_height="30dp"
    	halohoop:adbar_rawposition="4"
    	halohoop:crossline_color="#E8E8E8"
    	halohoop:drag_view_bg_color="#3300ff00"
    	halohoop:drag_view_bg_alpha="1.0">
    </com.halohoop.draggableadgridview.views.DraggableAdGridView>
-- --
###### 2.findViewById;
-- --
	setContentView(com.halohoop.dragadgridview.R.layout.activity_ad_main);
    DraggableAdGridView dgv = (DraggableAdGridView) findViewById(com.halohoop.dragadgridview.R.id.dgv);
    dgv.setAllowSwapAnimation(true);//允许动画 allow movement animation
-- --
###### 3.create adapter which is a extension of BaseDraggableAdAdapter<DataBeanType>;
-- --
    private class MyDraggableAdapter extends BaseDraggableAdAdapter<T> {
		private List<T> dataList;
	
        public MyDraggableAdapter(List<T> dataList) {
            super(MainAdActivity.this, dgv, dataList);
	    this.dataList = dataList;
        }

        @Override
        protected View getView4NormalIcon(int position) {
            //TODO add your own grid item view here
            return View;
        }

        @Override
        protected View getView4AdVp() {
            //TODO add your own ad bar view here,maybe ViewPager
            return View;
        }
    }
-- --
###### 4.implement methods;
* **①**.getView4NormalIcon(int position);//your grid item view;
* **②**.getView4AdVp();//your ad bar view;
* ![Markdown](http://i2.piimg.com/8359/158e754b6dfcdcb9.png)
###### 5.setAdapter;
-- --
	dgv.setAdapter(new MyDraggableAdapter(dataList));
-- --
###### 6.Free to go;


#Customization
####### in your xml file;
####### set xmlns first;
-- --
	xmlns:halohoop="http://schemas.android.com/apk/res-auto"
-- --
####### custom your own effect;
####### set your ad bar height:
* halohoop:adbar_height="30dp"
####### set your ad bar raw position:
* halohoop:adbar_rawposition="4"
####### set your grid cross line color:
* halohoop:crossline_color="#E8E8E8"
####### set your drag view bg color:
* halohoop:drag_view_bg_color="#3300ff00"
####### set your drag view bg alpha:
* halohoop:drag_view_bg_alpha="1.0"
#Compatibility
  
  * Android GINGERBREAD 2.3+
  
# Changelog

### Version: 1.0.0
  * Initial Build

## License

    Copyright 2016, Halohoop

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
