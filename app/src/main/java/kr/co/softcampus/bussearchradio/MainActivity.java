package kr.co.softcampus.bussearchradio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import java.net.URLEncoder;


public class MainActivity extends Activity {

    EditText edit;
    TextView text;
    String TAG = "TestActivity";
    RadioGroup mRgAllButtons;
    String stName;
    String StId;

    String data;
    StringBuffer buffer = new StringBuffer();
    int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit = (EditText)findViewById(R.id.edit);
        mRgAllButtons = findViewById(R.id.radiogroup);
    }

    public void addRadioButtons(String name) {//라디오 버튼 생성 메소드//
        mRgAllButtons.setOrientation(LinearLayout.VERTICAL);
        RadioButton rdbtn = new RadioButton(this);
        rdbtn.setId(View.generateViewId());
        rdbtn.setText(name);
        mRgAllButtons.addView(rdbtn);

    }

    public void mOnClick(View v) {
        switch (v.getId()) {

            case R.id.button:

                //Android 4.0 이상 부터는 네트워크를 이용할 때 반드시 Thread 사용해야 함
                new Thread(new Runnable(){

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        data = getStName();//아래 메소드를 호출하여 XML data를 파싱해서 String 객체로 얻어오기

                        //UI Thread(Main Thread)를 제외한 어떤 Thread도 화면을 변경할 수 없기때문에
                        //runOnUiThread()를 이용하여 UI Thread가 TextView 글씨 변경하도록 함
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                //ScrollView에 라디오 버튼 추가
                            }
                        });
                    }
                }).start();
                break;
        }
    }

    String getStName() {



        String str = edit.getText().toString();//EditText에 작성된 Text얻어오기
        String route=URLEncoder.encode(str);
        int number=0;

        String queryUrl = "http://apis.data.go.kr/6410000/busstationservice/getBusStationList?serviceKey=euEsltx738hhLpmcMAj7OzpwHp7BiX6f%2B6hCh2TM1pD37o9JO0MSRNahhC22%2BtsVGfgEibU5NMoOCU20%2BpuaBw%3D%3D&keyword="+route;

        try {
            URL url= new URL(queryUrl);//문자열로 된 요청 url을 URL 객체로 생성.
            InputStream is= url.openStream(); //url위치로 입력스트림 연결

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp= factory.newPullParser();
            xpp.setInput( new InputStreamReader(is, "UTF-8") ); //inputstream 으로부터 xml 입력받기

            String tag;
            xpp.next();
            int eventType= xpp.getEventType();

            while( eventType != XmlPullParser.END_DOCUMENT ){
                switch( eventType ){

                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("파싱 시작...\n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();//테그 이름 얻어오기


                        if(tag.equals("busStationList")) {
                            number = number + 1;
                            ;// 첫번째 검색결과

                        }else if(tag.equals("stationName")){
                            xpp.next();
                            stName=xpp.getText();
                            addRadioButtons(stName);//정류장 이름 입력인자로 하는 라디오버튼 생성함수 호출
                        }
                        else if(tag.equals("stationId")){
                            xpp.next();
                            StId=xpp.getText();//정류장 아이디
                        }

                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag= xpp.getName(); //테그 이름 얻어오기
                        if(tag.equals("busStationList")) buffer.append("\n");// 첫번째 검색결과종료..줄바꿈
                        break;
                }

                eventType= xpp.next();
            }

        } catch (Exception e) {
            buffer.append("예외 발생\n");
            e.printStackTrace();
            // TODO Auto-generated catch blocke.printStackTrace();
        }

        buffer.append("파싱 끝\n");
        return buffer.toString();//StringBuffer 문자열 객체 반환

    }//getXmlData method....

}//MainActivity class..
