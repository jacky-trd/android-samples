#include "Edge.h"


bool Edge::init(){
    Node::init();
    
    Size visibleSize = Director::getInstance()->getVisibleSize();
    setContentSize(visibleSize);
    setPhysicsBody(PhysicsBody::createEdgeBox(visibleSize));
    
    return true;
}