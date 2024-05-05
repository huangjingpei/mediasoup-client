package com.hiar.sdk.vslam;

public class AlgWrapper {

	// Load hiar slam sdk library
	static {
		System.loadLibrary("hiar");
		System.loadLibrary("HiAR3DTrack");
		System.loadLibrary("HiARSLAM");
	}

	public static boolean bInit = false;

	// Version
	public class HiarSlamVersion{
		public short major;
		public short minor;
		public short patch;
		public short build;

		public HiarSlamVersion(){
		}
	}
	public HiarSlamVersion version = new HiarSlamVersion(); // 保存当前sdk版本号

	// Camera intrinsics
	public class HiarSlamCameraIntrinsics{
		public int width;
		public int height;

		public float fx;
		public float fy;
		public float cx;
		public float cy;

		public float k1;
		public float k2;
		public float p1;
		public float p2;
		public float k3;

		public HiarSlamCameraIntrinsics(){
		}
	}
	public int[] CameraCount = new int[1]; // 保存sdk的数据库中针对当前设备信息的条目数量
	public HiarSlamCameraIntrinsics[] intrinsicsDB; // 保存sdk中针对当前设备的数据库
	public HiarSlamCameraIntrinsics intrinsics = new HiarSlamCameraIntrinsics(); // 保存当前设备的内参信息

	// Handle
	public long handle; // 保存sdk实例化后的句柄

	// Auxiliary function
	/**
	 * 获取sdk版本号
	 *
	 * version保存sdk版本号信息
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int GetVersion(){
		return hiarSlamGetVersion(version);
	}

	/**
	 * 获取sdk的数据库中针对当前设备信息的条目数量
	 *
	 * CameraCount保存设备信息的条目数量
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int GetSystemCameraIntrinsicsCount(){
		int r =  hiarSlamGetSystemCameraIntrinsicsCount(CameraCount);
		intrinsicsDB = new HiarSlamCameraIntrinsics[CameraCount[0]];
		return r;
	}

	/**
	 * 获取sdk中针对当前设备信息的数据库
	 *
	 * intrinsicsDB保存sdk中针对当前设备信息的数据库
	 *
	 * @param index
	 *            数据库中条目索引
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int GetSystemCameraIntrinsics(int index){
		intrinsicsDB[index] = new HiarSlamCameraIntrinsics();
		int r = hiarSlamGetSystemCameraIntrinsics(index, intrinsicsDB[index]);
		return r;
	}

	/**
	 * 获取sdk中针对当前设备相机的输出分辨率的相机内参
	 *
	 * intrinsics保存sdk中针对当前设备的内参信息
	 *
	 * @param width
	 *            当前设备相机的输出宽度
	 *
	 * @param height
	 *            当前设备相机的输出高度
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int GetPreferredSystemCameraIntrinsics(int width, int height){
		int r = hiarSlamGetPreferredSystemCameraIntrinsics(width, height, intrinsics);
		return r;
	}

	/**
	 * 针对当前设备相机的输出分辨率调整相机内参
	 *
	 * intrinsics保存当前设备的内参信息
	 *
	 * @param width
	 *            当前设备相机的输出宽度
	 *
	 * @param height
	 *            当前设备相机的输出高度
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int ResizeCameraIntrinsics(int width, int height) {
		return hiarSlamResizeCameraIntrinsics(intrinsics, width, height, intrinsics);
	}

	/**
	 * 将相机内参转换为OpenGL坐标系下的Projection Matrix
	 *
	 * @param nearPlane
	 *            渲染视锥体的近平面
	 *
	 * @param farPlane
	 *            渲染视锥体的远平面
	 *
	 * @param glProjection
	 *            OpenGL坐标系下的Projection Matrix
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int ConvertCameraIntrinsicsToGL(float nearPlane, float farPlane, float[] glProjection) {
		return hiarSlamConvertCameraIntrinsicsToGL(nearPlane, farPlane, intrinsics, glProjection);
	}

	/**
	 * 将sdk返回的相机外参转换为OpenGL坐标系下的ModelView Matrix
	 *
	 * @param extrinsics
	 *            由sdk返回的相机外参
	 *
	 * @param glModelView
	 *            OpenGL坐标系下的ModelView Matrix
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int ConvertCameraExtrinsicsToGL(float[] extrinsics, float[] glModelView) {
		return hiarSlamConvertCameraExtrinsicsToGL(extrinsics, glModelView);
	}

	/**
	 * 设置词袋文件路径
	 *
	 * @param vocFilePath
	 *            词袋文件路径
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int SetVocFilePath(String vocFilePath) {
		return hiarSlamSetVocFilePath(vocFilePath);
	}

	// Function
	/**
	 * 初始化sdk，获取sdk实例化后的句柄
	 *
	 * handle保存sdk实例化后的句柄
	 *
	 * @param initType
	 *              选择sdk初始化方式，参考{@link HiarSlamInitType}
	 *
	 * @param initFilePath
	 * 			    初始化文件路径
	 *            null：使用sdk默认路径
	 *            (识别图初始化：/sdcard/HiARSlam/HiARRecog.db，区域描述初始化：/sdcard/HiARSlam/HiARAreaDesc.dat, 模型初始化：/sdcard/HiARSlam/HiARModel.ham)
	 *
	 * @param mode
	 * 			    选择sdk工作模式，参考{@link HiarSlamMode}
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int Create(int initType, String initFilePath, int mode) {
		long[] handle_ = new long[1];
		int nRet = hiarSlamCreate(initType, initFilePath, mode, intrinsics, handle_);
		handle = handle_[0];
		bInit = (nRet == 0);
		return nRet;
	}

	/**
	 * 多图识别初始化sdk，获取sdk实例化后的句柄
	 *
	 * handle保存sdk实例化后的句柄
	 *
	 * @param initType
	 *              选择sdk初始化方式
	 *            1：识别图初始化
	 *
	 * @param initImagesPath
	 * 			    初始化文件路径数组
	 *
	 * @param mode
	 * 			    选择sdk工作模式
	 *            1：高精度模式，2：平衡模式，3：高性能模式
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int CreateWithMultiImages(int initType, String[] initImagesPath, int mode) {
		long[] handle_ = new long[1];
		int nRet = hiarSlamCreateWithMultiImages(initType, initImagesPath, mode, intrinsics, handle_);
		handle = handle_[0];
		bInit = (nRet == 0);
		return nRet;
	}

	/**
	 * 设置是否仅tracking,不做mapping
	 *
	 * @param onlyTracking
	 *            	是否仅tracking的标志
	 *            默认为false
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int SetOnlyTracking(boolean onlyTracking){
		return hiarSlamSetOnlyTracking(handle, onlyTracking);
	}

	/**
	 * 设置是否对pose做滤波
	 *
	 * @param useFilter
	 *            	是否对pose做滤波的标志
	 *            默认为false
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int SetUseFilter(boolean useFilter){
		return hiarSlamSetUseFilter(handle, useFilter);
	}

	/**
	 * 设置是否使用高光检测
	 *
	 * @param useHighLight
	 *            	是否使用高光检测的标志
	 *            默认为false
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int SetUseHighLight(boolean useHighLight){
		return hiarSlamSetUseHighLight(handle, useHighLight);
	}

	/**
	 * 获取识别到的图片的index.
	 * @param index
	 * 				[output] 识别到的图片的index
	 * @return 参考 {@link HiarSlamResultCode}
	 */
	public int GetInitImageIndex(int[] index){
		return hiarSlamGetInitImageIndex(handle, index);
	}

	/**
	 * 获取识别到的图片的信息.
	 * @param index
	 * 				[output] 识别到的图片的index
	 * @param width
	 * 				[output] 识别到的图片的width
	 * @param height
	 * 				[output] 识别到的图片的height
	 * @return 参考 {@link HiarSlamResultCode}
	 */
	public int GetInitModelInfo(int[] index, int[] width, int[] height){
		return hiarSlamGetInitModelInfo(handle, index, width, height);
	}

	/**
	 * 通过sdk处理当前图像帧，返回设备当前姿态以及sdk的跟踪状态
	 *
	 * @param imgBuffer
	 * 			    当前图像帧的Buffer数据
	 *
	 * @param imgFormat
	 *              图像格式，参考{@link HiarSlamImageFormat}
	 *
	 * @param pose
	 *              当前设备的姿态矩阵及跟踪状态
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int ProcessFrame(byte[] imgBuffer, int imgFormat, HiarSlamPose pose) {
		return hiarSlamProcessFrame(handle, imgBuffer, imgFormat, intrinsics.width, intrinsics.height, pose.extrinsics, pose.trackingState);
	}

	/**
	 * 保存sdk扫描过的区域描述信息
	 *
	 * @param areaDescFilePath
	 *            	区域描述文件保存路径
	 *            null：默认保存路径（/sdcard/HiARSlam）
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int SaveAreaDesc(String areaDescFilePath){
		return hiarSlamSaveAreaDesc(handle, areaDescFilePath);
	}

	/**
	 * 销毁sdk实例化句柄
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int Destroy(){
		bInit = false;
		return hiarSlamDestroy(handle);
	}

	/**
	 * 重置sdk实例化句柄
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int Reset(){
		bInit = false;
		return hiarSlamReset(handle);
	}

	/**
	 * 获取sdk当前的3D点信息
	 *
	 * @return null：获取失败，points：3D点数组,格式为(x1,y1,z1,x2,y2,z2,...)
	 *
	 * */
	public float[] GetPointCloudPoints() {
		int[] count = new int[1];
		int nRet = hiarSlamGetPointCloudPointCount(handle, count);
		if(nRet == -1){
			return null;
		}
		float[] points = new float[count[0] * 3];
		nRet = hiarSlamGetPointCloudPoints(handle, points, points.length);
		if(nRet == -1){
			return null;
		}
		return points;
	}

	/**
	 * 获取sdk 模型3D线段数量
	 *
	 * @param lineNum
	 *              线的数目
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int Get3DLineCount(int[] lineNum){
		return hiarSlamGet3DLineCount(handle, lineNum);
	}

	/**
	 * 获取sdk 模型3D线段信息
	 *
	 * @param lineArray
	 *              返回的线列表，6个为一组(x1,y1,z1,x2,y2,z2)
	 *
	 * @param lineNum
	 *              线的数目
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public int Get3DLines(float[] lineArray, int[] lineNum){
		return hiarSlamGet3DLines(handle, lineArray, lineNum);
	}

	/// Test function

	/**
	 * 从配置文件中读取模型初始化参数
	 *
	 * @param yamlFileName
	 *              配置文件路径
	 *
	 * @param mePara
	 *              模型初始化参数
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public static int ReadYamlFile(String yamlFileName, Object mePara) {
		return hiarSlamReadYamlFile(yamlFileName, mePara);
	}

	/**
	 * 将模型初始化参数写入yaml配置文件
	 *
	 * @param yamlFileName
	 *              配置文件路径
	 *
	 * @param mePara
	 *              模型初始化参数
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public static int WriteYamlFile(String yamlFileName, Object mePara) {
		return hiarSlamWriteYamlFile(yamlFileName, mePara);
	}

	/**
	 * 将模型初始化参数及初始化姿态写入yaml配置文件
	 *
	 * @param yamlFileName
	 *              配置文件路径
	 *
	 * @param mePara
	 *              模型初始化参数
	 *
	 * @param pose
	 *              初始化姿态
	 *
	 * @return 参考 {@link HiarSlamResultCode}
	 *
	 * */
	public static int WriteYamlFileWithPose(String yamlFileName, Object mePara, double[] pose) {
		return hiarSlamWriteYamlFileWithPose(yamlFileName, mePara, pose);
	}

	//Native auxiliary function
	private native int hiarSlamGetVersion(HiarSlamVersion version);

	private native int hiarSlamGetSystemCameraIntrinsicsCount(int[] count);

	private native int hiarSlamGetSystemCameraIntrinsics(int index, HiarSlamCameraIntrinsics intrinsics);

	private native int hiarSlamGetPreferredSystemCameraIntrinsics(int width, int height, HiarSlamCameraIntrinsics intrinsics);

	private native int hiarSlamResizeCameraIntrinsics(HiarSlamCameraIntrinsics input, int width, int height, HiarSlamCameraIntrinsics output);

	private native int hiarSlamConvertCameraIntrinsicsToGL(float nearPlane, float farPlane, HiarSlamCameraIntrinsics intrinsics, float[] glProjection);

	private native int hiarSlamConvertCameraExtrinsicsToGL(float[] extrinsics, float[] glModelView);

	private native int hiarSlamSetVocFilePath(String vocFilePath);

	private static native int hiarSlamReadYamlFile(String yamlFileName, Object mePara);

	private static native int hiarSlamWriteYamlFile(String yamlFileName, Object mePara );

	private static native int hiarSlamWriteYamlFileWithPose(String yamlFileName, Object mePara, double[] pose);

 	//Native function
	private native int hiarSlamCreate(int initType, String initFilePath, int mode, HiarSlamCameraIntrinsics intrinsics, long[] handle);

	private native int hiarSlamCreateWithMultiImages(int initType, String[] initImagesPath, int mode, HiarSlamCameraIntrinsics intrinsics, long[] handle);

	private native int hiarSlamSetOnlyTracking(long handle, boolean onlyTracking);

	private native int hiarSlamSetUseFilter(long handle, boolean useFilter);

	private native int hiarSlamSetUseHighLight(long handle, boolean useHighLight);

	private native int hiarSlamGetInitImageIndex(long handle, int[] index);

	private native int hiarSlamGetInitModelInfo(long handle, int[] index, int[] width, int[] height);

	private native int hiarSlamProcessFrame(long handle, byte[] imgBuffer, int imgFormat, int width, int height, float[] matRT, int[] trackingState);

	private native int hiarSlamSaveAreaDesc(long handle, String areaDescFilePath);

	private native int hiarSlamDestroy(long handle);

	private native int hiarSlamReset(long handle);

	private native int hiarSlamGetPointCloudPointCount(long handle, int[] count);

	private native int hiarSlamGetPointCloudPoints(long handle, float[] points, int count);

	private native int hiarSlamGet3DLineCount(long handle, int[] lineNum);

	private native int hiarSlamGet3DLines(long handle, float[] lineArray, int[] lineNum);

}
