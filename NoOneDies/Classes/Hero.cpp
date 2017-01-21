#include "Hero.h"
#include "FlashTool.h"

class FlashTool;


bool Hero::init(){
    
    Sprite::init();
    
    Size s = Size(44, 52);
    
    setContentSize(s);
    setPhysicsBody(PhysicsBody::createBox(s));
    setScale(0.5);

	runAction(RepeatForever::create(FlashTool::readJsonSpriteSheet("Hero.json", 0.2f)));
    
    getPhysicsBody()->setRotationEnable(false);
    getPhysicsBody()->setContactTestBitmask(1);
    return true;
    
}