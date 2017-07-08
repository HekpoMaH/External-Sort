#include<bits/stdc++.h>
using namespace std;
int main(){
    string command="./compile.sh";
    system(command.c_str());
    for(int i=1; i<18; i++){
        string f1="TestSuite/test"+to_string(i)+"a.dat";
        string f2="TestSuite/test"+to_string(i)+"b.dat";
        command="./run.sh " + f1 + " " + f2;
        cout<<i<<"\n";//command<<"\n";
        system(command.c_str());
    }
}
