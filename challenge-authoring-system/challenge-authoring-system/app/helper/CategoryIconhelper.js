import category1 from '../ressources/icons/categoryIcons/01_web_security.png';
import category2 from '../ressources/icons/categoryIcons/02_exploitation.png';
import category3 from '../ressources/icons/categoryIcons/03_crypto.png';
import category4 from '../ressources/icons/categoryIcons/04_reverse_engineering.png';
import category5 from '../ressources/icons/categoryIcons/05_forensic.png';
import category6 from '../ressources/icons/categoryIcons/06_penetration_testing.png';
import category7 from '../ressources/icons/categoryIcons/07_network_security.png';
import category8 from '../ressources/icons/categoryIcons/08_database_security.png';
import category9 from '../ressources/icons/categoryIcons/09_malware.png';
import category10 from '../ressources/icons/categoryIcons/10_programming.png';
import category11 from '../ressources/icons/categoryIcons/11_linux.png';
import category12 from '../ressources/icons/categoryIcons/12_windows.png';
import category13 from '../ressources/icons/categoryIcons/13_osx.png';
import category14 from '../ressources/icons/categoryIcons/14_android.png';
import category15 from '../ressources/icons/categoryIcons/15_ios.png';
import category16 from '../ressources/icons/categoryIcons/16_wireless.png';
import category17 from '../ressources/icons/categoryIcons/17_phone.png';
import category18 from '../ressources/icons/categoryIcons/18_defense.png';
import category19 from '../ressources/icons/categoryIcons/19_fun.png';
import category20 from '../ressources/icons/categoryIcons/20_social_engineering.png';
import category21 from '../ressources/icons/categoryIcons/21_open_source_intelligence.png';

const categoryIcons = [category1, category2, category3,
                        category4, category5, category6,
                        category7, category8, category9,
                        category10, category11, category12,
                        category13, category14, category15,
                        category16, category17, category18,
                        category19, category20, category21];


const CategoryIconHelper = (iconId) => {
    const icon =  categoryIcons[iconId - 1];
    if(icon !== undefined && icon !== null) {
        return icon;
    }
    return null;
};

export default CategoryIconHelper;
