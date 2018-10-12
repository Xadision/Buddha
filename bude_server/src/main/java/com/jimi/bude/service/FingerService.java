package com.jimi.bude.service;

import com.jimi.bude.exception.OperationException;
import com.jimi.bude.model.Arm;
import com.jimi.bude.model.Bead;
import com.jimi.bude.model.Finger;
import com.jimi.bude.util.ResultUtil;

public class FingerService {
	private static final String SELECT_EXIST_FINGER_SQL = "select * from finger where name = ? and arm_name = ?";
	public static final FingerService me = new FingerService();

	public ResultUtil add(String fingerName, String armName, Integer beadId) {
		Arm arm = Arm.dao.findById(armName);
		String result = "";
		if (arm == null) {
			result = "the arm is not exist";
			throw new OperationException(result);
		}
		Bead bead = Bead.dao.findById(beadId);
		if (bead == null) {
			result = "the bead is not exist";
			throw new OperationException(result);
		}
		Finger finger = select(fingerName, armName);
		if (finger != null) {
			result = "the finger is already exist";
			throw new OperationException(result);
		}
		finger = new Finger();
		finger.setArmName(armName).setName(fingerName).setBeadId(beadId).save();
		return ResultUtil.succeed();
	}

	public ResultUtil delete(String fingerName, String armName) {
		String result = "";
		Finger finger = Finger.dao.findFirst(SELECT_EXIST_FINGER_SQL, fingerName, armName);
		if (finger == null) {
			result = "the finger is not exist";
			throw new OperationException(result);
		}
		try {
			finger.delete();
		} catch (Exception e) {
			result = "the finger has been referenced";
			throw new OperationException(result);
		}
		return ResultUtil.succeed();
	}

	public Finger select(String fingerName, String armName) {
		Finger finger = Finger.dao.findFirst(SELECT_EXIST_FINGER_SQL, fingerName, armName);
		return finger;
	}

	public void update(Finger finger) {
		finger.update();
	}
}
