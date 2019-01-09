package com.jimi.bude.controller;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import com.jimi.bude.annotation.Open;
import com.jimi.bude.exception.ParameterException;
import com.jimi.bude.service.BeadService;
import com.jimi.bude.util.CommonUtil;
import com.jimi.bude.util.ResultUtil;


/**
 * 软件包管理控制层
 * @type BeadController
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年8月28日
 */
public class BeadController extends Controller {

	private final static Integer SUFFIXTIME_LENGTH = 12;
	
	private final static Integer MD5_LENGTH = 32;
	
	private final static Integer YEAR_INDEX = 4;
	
	private final static Integer MONTH_INDEX = 6;
	
	private final static Integer DAY_INDEX = 8;
	
	private final static Integer HOUR_INDEX = 10;
	
	private final  static Integer MINUTE_INDEX = 12;
	private BeadService beadService = BeadService.me;

	
	/**
	 * 上传文件
	 * @param file 文件
	 * @param alias 别名
	 * @param updateDescribe 更新描述
	 * @param md5 文件md5校验码
	 */
	public synchronized void upload(UploadFile file, String alias, String updateDescribe, String md5) {
		try {
			file = getFile();
			if (file == null) {
				throw new ParameterException("the uploadFile is null");
			}
			String fileName = file.getFileName();
			alias = getPara("alias");
			updateDescribe = getPara("updateDescribe");
			String headName = "";
			String faceName = "";
			String suffixTime = "";
			Integer firstCode = null;
			Integer secondCode = null;
			Integer debugCode = null;
			try {
				String[] array = fileName.split("_");
				String[] name = array[0].split("-");
				String[] version = array[1].split("\\.");
				headName = name[0];
				faceName = name[1];
				firstCode = Integer.valueOf(version[0]);
				secondCode = Integer.valueOf(version[1]);
				debugCode = Integer.valueOf(version[2]);
				suffixTime = array[2].split("\\.")[0];
				if (suffixTime.length() != SUFFIXTIME_LENGTH || !checkSuffixTime(suffixTime)) {
					throw new ParameterException("fileName is not in the right format");
				}
			} catch (Exception e) {
				throw new ParameterException("fileName is not in the right format");
			}
			if (alias == null || updateDescribe == null || md5 == null) {
				throw new ParameterException("Parameters can not be null");
			}
			if (md5.trim().equals("") || md5.length() != MD5_LENGTH) {
				throw new ParameterException("md5 is not in the right format");
			}
			ResultUtil result = beadService.upload(file.getFile(), fileName, headName, faceName, firstCode, secondCode, debugCode, suffixTime, alias, updateDescribe, md5);
			renderJson(result);
		} finally {
			if (file != null) {
				file.getFile().delete();
			}
		}

	}


	/**
	 * 下载软件包
	 * @param headName 项目名
	 * @param faceName 模块名
	 * @param firstCode 主版本号
	 * @param secondCode 次版本号
	 * @param debugCode 修正版本号
	 * @param suffixTime 后缀时间
	 */
	@Open
	public void download(String headName, String faceName, Integer firstCode, Integer secondCode, Integer debugCode, String suffixTime) {
		if (headName == null || faceName == null || firstCode == null || secondCode == null || debugCode == null || suffixTime == null) {
			renderNull();
			return;
		}
		String fileName = CommonUtil.getFileName(headName, faceName, firstCode.toString(), secondCode.toString(), debugCode.toString(), suffixTime);
		String filePath = CommonUtil.getFilePath("BEAD", headName, faceName);
		File file = CommonUtil.findFile(filePath, fileName);
		if (file == null) {
			renderNull();
			// renderJson(ResultUtil.failed(501, "file is not exist"));
			return;
		}
		renderFile(file);
	}


	/**
	 * 更新软件包信息
	 * @param beadId
	 * @param alias 别名
	 * @param updateDescribe 更新描述
	 */
	public void update(Integer beadId, String alias, String updateDescribe) {
		if (beadId == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = beadService.update(beadId, alias, updateDescribe);
		renderJson(result);
	}


	/**
	 * 查询主版本号
	 * @param faceId 模块ID
	 */
	public void selectFirstCode(Integer faceId) {
		if (faceId == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = beadService.selectFirstCode(faceId);
		renderJson(result);
	}


	/**
	 * 查询次版本号
	 * @param faceId 模块ID
	 * @param firstCode 主版本号
	 */
	public void selectSecondCode(Integer faceId, Integer firstCode) {
		if (faceId == null || firstCode == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = beadService.selectSecondCode(faceId, firstCode);
		renderJson(result);
	}


	/**
	 * 查询修正版本号
	 * @param faceId 模块ID
	 * @param firstCode 主版本号
	 * @param secondCode 次版本号
	 */
	public void selectDebugCode(Integer faceId, Integer firstCode, Integer secondCode) {
		if (faceId == null || firstCode == null || secondCode == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = beadService.selectDebugCode(faceId, firstCode, secondCode);
		renderJson(result);
	}


	/**
	 * 查询后缀时间
	 * @param faceId 模块ID
	 * @param firstCode 主版本号
	 * @param secondCode 次版本号
	 * @param debugCode 修正版本号
	 */
	public void selectSuffixTime(Integer faceId, Integer firstCode, Integer secondCode, Integer debugCode) {
		if (faceId == null || firstCode == null || secondCode == null || debugCode == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = beadService.selectSuffixTime(faceId, firstCode, secondCode, debugCode);
		renderJson(result);
	}


	/**
	 * 查询软件包信息
	 * @param faceId 模块ID
	 * @param firstCode 主版本号(可空)
	 * @param secondCode 次版本号(可空)
	 * @param debugCode 修正版本号(可空)
	 */
	public void select(Integer faceId, Integer firstCode, Integer secondCode, Integer debugCode, String suffixTime, Integer currentPage, Integer pageSize) {
		if (faceId == null) {
			throw new ParameterException("faceId can not be null");
		}
		if ((currentPage == null || pageSize == null) && !(currentPage == null && pageSize == null)) {
			throw new ParameterException("currentPage and pageSize should either empty or not empty");
		}
		if (!(currentPage == null && pageSize == null) && (currentPage <= 0 || pageSize <= 0)) {
			throw new ParameterException("currentPage and pageSize must be greater than 0");
		}
		ResultUtil result = beadService.select(faceId, firstCode, secondCode, debugCode, suffixTime, currentPage, pageSize);
		renderJson(result);
	}


	/**
	 * 删除软件包
	 * @param beadId
	 */
	public void delete(Integer beadId) {
		if (beadId == null) {
			throw new ParameterException("beadId can not be null");
		}
		ResultUtil result = beadService.delete(beadId);
		renderJson(result);
	}


	private boolean checkSuffixTime(String suffixTime) {
		String year = suffixTime.substring(0, YEAR_INDEX);
		String month = suffixTime.substring(YEAR_INDEX, DAY_INDEX);
		String day = suffixTime.substring(DAY_INDEX, HOUR_INDEX);
		String hour = suffixTime.substring(HOUR_INDEX, MINUTE_INDEX);
		String minute = suffixTime.substring(MINUTE_INDEX, SUFFIXTIME_LENGTH);
		String time = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":00";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			formatter.parse(time);
		} catch (ParseException e) {
			return false;
		}
		return true;

	}
}
