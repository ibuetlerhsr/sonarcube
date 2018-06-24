import noviceImage from '../ressources/images/novice.PNG';
import easyImage from '../ressources/images/easy.PNG';
import mediumImage from '../ressources/images/medium.PNG';
import hardImage from '../ressources/images/hard.PNG';
import leetImage from '../ressources/images/leet.png';
import notFoundIMage from '../ressources/images/notFound.png';
import trainingImage from '../ressources/icons/training.svg';
import ctfImage from '../ressources/icons/ctf.svg';
import liveDemoImage from '../ressources/icons/liveDemo.svg';
import challengeSuccess from '../ressources/icons/checked2.svg';

const images = {
    'novice': noviceImage,
    'easy': easyImage,
    'medium': mediumImage,
    'hard': hardImage,
    'leet': leetImage,
    'ctf': ctfImage,
    'training': trainingImage,
    'live-demo': liveDemoImage,
    'notFound': notFoundIMage,
    'challengeSuccess': challengeSuccess
};

const ImageHelper = (imageName) => {
    const image =  images[imageName];
    if(image !== undefined && image !== null) {
        return image;
    }
    return images.notFound;
};

export default ImageHelper;
