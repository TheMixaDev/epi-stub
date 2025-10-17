size1 = 0.5;
size2 = 0.5;
function changeSize(panel, percent) {
    if(percent < 0 || percent > 1)
        return;
    if(panel === 1) {
        size1 = percent;
        size2 = 1 - percent;
    } else {
        size2 = percent;
        size1 = 1 - percent;
    }
}
