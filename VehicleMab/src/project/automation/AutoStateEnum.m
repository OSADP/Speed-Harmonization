classdef AutoStateEnum < Simulink.IntEnumType
  enumeration
    MANUAL(0)
    AUTO_FULL(1)
    AUTO_LOCAL(2)
  end
  methods (Static)
    function retVal = getDefaultValue()
      retVal = AutoStateEnum.MANUAL;
    end
  end
end 