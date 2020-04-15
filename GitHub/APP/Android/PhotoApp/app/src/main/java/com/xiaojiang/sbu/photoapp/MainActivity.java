package com.xiaojiang.sbu.photoapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public static final int TAKE_PHOTO = 1;

    public static final int CHOOSE_PHOTO = 2;

    private ImageView picture;

    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button takePhoto = (Button) findViewById(R.id.take_photo);
        picture = (ImageView) findViewById(R.id.picture);
        Button chooseFromAlbum = (Button) findViewById(R.id.choose_from_album);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建File对象,用于存储拍照后的照片
                //安卓6.0 读写sd被称为危险权限 所以存到缓存目录 而getExternalCacheDir 就可以得到这个缓存目录
                File fileDir = getExternalCacheDir();
                File imageDir = new File(fileDir,"images");
                if(!imageDir.exists()){
                    imageDir.mkdirs();
                }
                File imageFile = new File(imageDir,"picture.jpg");

                try{
                    //判断文件是否存在
                    if(imageFile.exists()){
                        //文件存在就删除
                        imageFile.delete();
                    }
                    //创建图片
                    imageFile.createNewFile();
                }catch (Exception e){
                    e.printStackTrace();
                }

                if(Build.VERSION.SDK_INT >= 24){
                    //如果设备版本大于7.0

                    imageUri = FileProvider.getUriForFile(MainActivity.this,
                            "com.example.cameraalbumtest.fileprovider",imageFile);
                } else {

                    imageUri = Uri.fromFile(imageFile);
                }

                //IMAGE_CAPTURE:图片捕获
                //MediaStore:媒体商店
                //EXTRA_OUTPUT:额外输出
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
            }

        });

        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                } else {
                    openAlbum();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //requestCode 发出去的请求码   resultCode 返回的参数
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                } else {
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    try{
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    if(Build.VERSION.SDK_INT >= 19){
                        //4.4 及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    }else{
                        //4.4 及以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        //data是从相册返回的数据
        //android 7.1.1
        //uri == content://com.android.providers.media.documents/document/image%3A75
        //uri.getAuthority() == com.android.providers.media.documents
        //uri.getPath() == /document/image:75
        //DocumentsContract.getDocumentId(uri) == image:75
        //MediaStore.Images.Media.EXTERNAL_CONTENT_URI == content://media/external/images/media
        //真实路径 path == /storage/emulated/0/Download/picture.jpg

        //android4.4
        //uri == content://com.android.providers.media.documents/document/image%3A28
        //uri.getAuthority() == com.android.providers.media.documents
        //uri.getPath() == /document/image:28
        //DocumentsContract.getDocumentId(uri) == image:28
        //MediaStore.Images.Media.EXTERNAL_CONTENT_URI == content://media/external/images/media
        //真实路径 path == /storage/sdcard/images/picture.jpg

        //相册存了图片的id，并没有存实际路径。
        //Authority就是相册数据库的标识符，这里有两个数据库，他们的标识符分别为
        //com.android.providers.media.documents
        //com.android.providers.downloads.documents
        //当点击一张照片它会返回document封装了的uri，然后进行解析出资源id，
        //然后根据id在MediaStore数据库中获取真实URL路径

        //判断该Uri是否是document封装过的
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的Uri,则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){

                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" +id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);

            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                //这个方法负责把id和contentUri连接成一个新的Uri
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri,则使用普通方式处理
            imagePath =getImagePath(uri,null);

        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri,直接获取图片路径即可
            imagePath = uri.getPath();
        }

        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        //通过Uri和selection来获取真实路径
        //Android系统提供了MediaScanner，MediaProvider，MediaStore等接口，并且提供了一套数据库
        //表格，通过Content Provider的方式提供给用户。当手机开机或者有SD卡插拔等事件发生时，系统
        //将会自动扫描SD卡和手机内存上的媒体文件，如audio，video，图片等，将相应的信息放到定义好
        //的数据库表格中。在这个程序中，我们不需要关心如何去扫描手机中的文件，只要了解如何查询和使
        //用这些信息就可以了。MediaStore中定义了一系列的数据表格，通过ContentResolver提供的查询
        //接口，我们可以得到各种需要的信息。
        //EXTERNAL_CONTENT_URI 为查询外置内存卡的，INTERNAL_CONTENT_URI为内置内存卡。
        //MediaStore.Audio获取音频信息的类
        //MediaStore.Images获取图片信息
        //MediaStore.Video获取视频信息
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToNext()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return  path;
    }

    private void displayImage(String imagePath){
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT);
        }
    }
}

