package bgby.skynet.org.smarthomeui.uicontroller;

public enum UIControllerStatus {
	STARTED_INITIAL("启动", 0.10, "启动参数加载完成"),
	INVALID_PARAMS("启动参数错误", "启动参数校验失败", false),
	START_SERVICES_FAILED("服务启动失败", "无法启动后台服务", false),
	REQUEST_LAYOUT("请求布局", 0.12, "正在向上位机请求本机布局设置..."),
	NO_DRIVER_PROXY("上位机不在线", "无法连接到指定的上位机", false),
	REQUEST_LAYOUT_FAIL("请求失败", "从上位机请求布局失败", false),
	NO_LAYOUT("没有布局", "没有找到本控制屏的布局", false),
	VERIFY_LAYOUT("布局验证", 0.15, "正在检查获得的布局配置..."),
	INVALID_LAYOUT("无效布局", "布局验证失败，无法加载", false),
	REQUEST_DEVICE_DATA("请求设备数据",0.30, "正在向上位机请求设备数据..."),
	REQUEST_DEVICE_DATA_FAIL("设备数据错误", "请求设备数据发生错误", false),
	VERIFY_DEVICE_DATA("设备数据校验",0.50,"正在校验设备数据..."),
	INVALID_DEVICE_DATA("设备数据错误","设备数据校验出错", false),
	CREATE_LAYOUT("加载设备",0.60,"正在加载控制设备数据..."),
	INIT_DEVICES("初始化设备",0.90,"正在初始化设备数据"),
	START_COMPLETED("完成","启动完成", true);
	
	
	protected String name;
	protected double percentage;
	protected String description;
	protected boolean completed;
	protected boolean success;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	UIControllerStatus(String name, double percentage, String description){
		this.name= name;
		this.percentage = percentage;
		this.description = description;
		completed = false;
		success = true;
	}
	
	UIControllerStatus(String name, String description, boolean success){
		this.name= name;
		this.percentage = 1.0;
		this.description = description;
		completed = true;
		this.success = success;
	}
}
