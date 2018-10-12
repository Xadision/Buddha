let url;
if (process.env.NODE_ENV === 'production') {
  url = window.g.API_URL
} else {
  url = window.g.LOCAL_URL
}

/*head*/
let head = url + '/head'
export const addHeadUrl = head + '/add'
export const deleteHeadUrl = head + '/delete'
export const updateHeadUrl = head + '/update'
export const selectHeadUrl = head + '/select'

/*face*/
let face = url + '/face'
export const addFaceUrl = face + '/add'
export const deleteFaceUrl = face + '/delete'
export const updateFaceUrl = face + '/update'
export const selectFaceUrl = face + '/select'
export const selectFingerUrl = face + '/selectFinger'
export const publishUrl = face + '/publish'

/*bead*/
let bead = url + '/bead'
export const uploadBeadUrl = bead + '/upload'
export const selectBeadUrl = bead + '/select'
export const updateBeadUrl = bead + '/update'
export const selectFirstCodeUrl = bead + '/selectFirstCode'
export const selectSecondCodeUrl = bead + '/selectSecondCode'
export const selectDebugCodeUrl = bead + '/selectDebugCode'
export const selectSuffixTimeUrl = bead + '/selectSuffixTimeCode'
export const deleteBeadUrl = bead + '/delete'
export const downloadBeadUrl = bead + '/download'

/*finger*/
let finger = url + '/finger'
export const addFingerUrl = finger + '/add'
export const deleteFingerUrl = finger + '/delete'

/*arm*/
let arm = url + '/arm'
export const addArmUrl = arm + '/add'
export const deleteArmUrl = arm + '/delete'
export const selectArmUrl = arm + '/select'
export const selectArmFingerUrl = arm + '/selectFinger'
export const editBodyUrl = arm + '/editBody'

/*body*/
let body = url + '/body'
export const addBodyUrl = body + '/add'
export const deleteBodyUrl = body + '/delete'
export const updateBodyUrl = body + '/update'
export const selectBodyUrl = body + '/select'
export const selectBodyArmUrl = body + '/selectArm'

